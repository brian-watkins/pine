package org.pine

import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.TestClass

import java.lang.annotation.Annotation
import java.lang.reflect.Field
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
        return Arrays.asList(this.specClass.getFields()).stream()
                .filter({ field -> field.isAnnotationPresent(annotationClass) })
                .map({ field -> getFieldValue(field, specScriptInstance) })
                .filter({ fieldValue -> valueClass.isInstance(fieldValue) })
                .collect(Collectors.toList())
    }

    private Object getFieldValue(Field field, Object instance) {
        if (instance != null || Modifier.isStatic(field.getModifiers())) {

            Object value = field.get(instance)
            println "Getting instance of ${value} from instance: ${instance}"

            return value
        }

        return null;
    }

    @Override
    public List<FrameworkMethod> getAnnotatedMethods() {
        return Arrays.asList(this.specClass.getMethods()).stream()
                .filter({ method -> method.getAnnotations().size() > 0 })
                .map({method -> new FrameworkMethod(method) })
                .collect(Collectors.toList())
    }

    @Override
    public List<FrameworkMethod> getAnnotatedMethods(Class <? extends Annotation> annotationClass) {
        return getAnnotatedMethods().stream()
                .filter({ method -> method.getMethod().isAnnotationPresent(annotationClass) })
                .collect(Collectors.toList())
    }

    @Override
    public <T> List<T> getAnnotatedMethodValues(Object specScriptInstance,
                                                Class<? extends Annotation> annotationClass, Class<T> valueClass) {
        return getAnnotatedMethods(annotationClass).stream()
                .filter({ method -> valueClass.isAssignableFrom(method.getReturnType()) })
                .map({ method -> invokeAnnotatedMethod(method, specScriptInstance) })
                .collect(Collectors.toList())
    }

    private Object invokeAnnotatedMethod(FrameworkMethod method, Object instance) {
        if (instance != null || Modifier.isStatic(method.getMethod().getModifiers())) {
            return method.invokeExplosively(instance)
        }

        return null;
    }
}
