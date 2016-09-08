package org.pine

import org.junit.runners.model.TestClass
import java.lang.annotation.Annotation
import java.lang.reflect.Field
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
        return Arrays.asList(this.specClass.getFields()).stream()
                .filter({ field -> field.getAnnotationsByType(annotationClass).size() > 0 })
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
    public <T> List<T> getAnnotatedMethodValues(Object specScriptInstance,
                                                Class<? extends Annotation> annotationClass, Class<T> valueClass) {
        return Arrays.asList(this.specClass.getMethods()).stream()
                .filter({ method -> method.getAnnotationsByType(annotationClass).size() > 0 })
                .filter({ method -> valueClass.isAssignableFrom(method.getReturnType()) })
                .map({ method -> invokeAnnotatedMethod(method, specScriptInstance) })
                .collect(Collectors.toList())
    }

    private Object invokeAnnotatedMethod(Method method, Object instance) {
        if (instance != null || Modifier.isStatic(method.getModifiers())) {
            return method.invoke(instance)
        }

        return null;
    }
}
