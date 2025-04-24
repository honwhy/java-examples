package com.honwhy.examples.common.annotation;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.Set;

@SupportedAnnotationTypes({"com.honwhy.examples.common.annotation.AtVersions"})
public class AtVersionProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("AtVersionProcessor---");

        // 获取所有被 AtVersion 注解的元素
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(AtVersions.class);
        System.out.println("elements: " + elements);

        elements.forEach(this::generateCode);

        return false;
    }

    /**
     * 通过元素生成代码
     * @param element
     */
    private void generateCode(Element element) {
        AtVersion[] annotation = element.getAnnotationsByType(AtVersion.class);
        for (AtVersion atVersion : annotation) {
            String className = atVersion.value();
            String packageName = getPackageName(element);
            String fullClassName = packageName + ".AtVersion" + className;

            // 创建 ClassWriter
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC + Opcodes.ACC_INTERFACE, fullClassName.replace(".", "/"), null, "java/lang/Object", null);

            // 完成类的生成
            cw.visitEnd();

            // 使用 Filer 创建新的类文件
            try {
                JavaFileObject classFile = processingEnv.getFiler().createClassFile(fullClassName);
//                FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, packageName, path);
                try (OutputStream outputStream = classFile.openOutputStream()) {
                    outputStream.write(cw.toByteArray());
                    outputStream.flush();
                    System.out.println("Generated interface: " + fullClassName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取元素所属的包名，兼容低版本 JDK。
     *
     * @param element 当前元素
     * @return 包名
     */
    private String getPackageName(Element element) {
        while (element != null && !(element instanceof PackageElement)) {
            element = element.getEnclosingElement();
        }
        return element != null ? ((PackageElement) element).getQualifiedName().toString() : "";
    }
}