package org.pine.util

import java.lang.annotation.Annotation
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.stream.Collectors

class ReflectionUtils {

    public static <T> List<T> getAnnotatedFieldValues(Class clazz, Object instance, Class<? extends Annotation> annotationClass, Class<T> valueClass) {
        return Arrays.asList(clazz.getFields()).stream()
                .filter({ field -> field.isAnnotationPresent(annotationClass) })
                .map({ field -> getFieldValue(field, instance) })
                .filter({ fieldValue -> valueClass.isInstance(fieldValue) })
                .collect(Collectors.toList())
    }

    private static Object getFieldValue(Field field, Object instance) {
        if (instance != null || Modifier.isStatic(field.getModifiers())) {

            Object value = field.get(instance)
            println "Getting instance of ${value} from instance: ${instance}"

            return value
        }

        return null;
    }
}
