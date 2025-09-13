package com.honwhy.examples.qrcode.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${forward.target.baseurl}")
    private String targetBaseUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public FilterRegistrationBean<ContentCachingFilter> contentCachingFilter() {
//        FilterRegistrationBean<ContentCachingFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new ContentCachingFilter());
//        registrationBean.addUrlPatterns("/*");
//        return registrationBean;
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestForwardInterceptor(restTemplate(), targetBaseUrl))
                .addPathPatterns("/**");
    }
}
