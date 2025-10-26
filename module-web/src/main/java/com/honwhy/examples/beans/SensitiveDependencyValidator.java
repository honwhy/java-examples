package com.honwhy.examples.beans;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class SensitiveDependencyValidator implements SmartInitializingSingleton, BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // Spring 会在创建这个 Bean 时自动调用这个方法
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
        } else {
            throw new IllegalStateException("❌ 当前 BeanFactory 不是 ConfigurableListableBeanFactory 类型，无法执行依赖检查。");
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (beanFactory == null) {
            System.err.println("⚠️ BeanFactory 未注入，跳过敏感依赖检查。");
            return;
        }

        // 1️⃣ 收集所有带 @SensitiveComponent 注解的 Bean
        Map<String, Object> allBeans = beanFactory.getBeansOfType(Object.class);
        Map<String, Class<?>> sensitiveBeans = new HashMap<>();

        allBeans.forEach((name, bean) -> {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            if (targetClass.isAnnotationPresent(SensitiveComponent.class)) {
                sensitiveBeans.put(name, targetClass);
            }
        });

        if (sensitiveBeans.isEmpty()) {
            System.out.println("✅ 未发现 @SensitiveComponent Bean，无需检查。");
            return;
        }

        // 2️⃣ 检查所有 Bean 的依赖关系
        List<String> violations = new ArrayList<>();

        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            String[] dependencies = beanFactory.getDependenciesForBean(beanName);

            for (String dep : dependencies) {
                if (sensitiveBeans.containsKey(dep)) {
                    violations.add(String.format(
                            "Bean [%s] 依赖了敏感 Bean [%s:%s]",
                            beanName,
                            dep,
                            ClassUtils.getShortName(sensitiveBeans.get(dep))
                    ));
                }
            }
        }

        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .collect(Collectors.joining("\n  ", "\n  ", ""));
            throw new IllegalStateException("❌ 检测到以下敏感依赖，Spring 启动中止：" + message);
        }

        System.out.println("✅ 敏感 Bean 依赖检查通过。");
    }
}
