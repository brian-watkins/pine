package org.pine.util

import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.TestClass
import org.pine.util.ReflectionUtils

import java.lang.annotation.Annotation
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.stream.Collectors

public class SpecClass extends TestClass {

    Class specClass;

    public SpecClass(Class specClass) {
        super(null);

        this.specClass = specClass;
    }

    @Override
    public String getName() {
        return specClass.getName()
    }


    @Override
    public <T> List<T> getAnnotatedFieldValues(Object specScriptInstance, Class<? extends Annotation> annotationClass, Class<T> valueClass) {
        ReflectionUtils.getAnnotatedFieldValues(this.specClass, specScriptInstance, annotationClass, valueClass)
    }

    @Override
    public List<FrameworkMethod> getAnnotatedMethods() {
        return Arrays.asList(this.specClass.getMethods()).stream()
                .filter({ Method method -> method.getAnnotations().size() > 0 })
                .map({ Method method -> new FrameworkMethod(method) })
                .collect(Collectors.toList())
    }

    @Override
    public List<FrameworkMethod> getAnnotatedMethods(Class <? extends Annotation> annotationClass) {
        return getAnnotatedMethods().stream()
                .filter({ FrameworkMethod method -> method.getMethod().isAnnotationPresent(annotationClass) })
                .collect(Collectors.toList())
    }

    @Override
    public <T> List<T> getAnnotatedMethodValues(Object specScriptInstance,
                                                Class<? extends Annotation> annotationClass, Class<T> valueClass) {
        return getAnnotatedMethods(annotationClass).stream()
                .filter({ FrameworkMethod method -> valueClass.isAssignableFrom(method.getReturnType()) })
                .map({ FrameworkMethod method -> invokeAnnotatedMethod(method, specScriptInstance) })
                .collect(Collectors.toList())
    }

    private Object invokeAnnotatedMethod(FrameworkMethod method, Object instance) {
        if (instance != null || Modifier.isStatic(method.getMethod().getModifiers())) {
            return method.invokeExplosively(instance)
        }

        return null;
    }
}
