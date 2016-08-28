package org.pine.testHelpers

import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement

class TestInjectRule implements MethodRule {

    @Override
    Statement apply(Statement base, FrameworkMethod method, Object target) {

        Arrays.asList(target.class.getDeclaredFields()).stream()
            .filter({ field -> field.getAnnotationsByType(TestInject).size() > 0 })
            .forEach({ field ->
                println "Setting FunSpecDelegate on @TestInject field named: ${field.getName()}"
                field.set(target, new FunSpecDelegate() )
            }
        )

        return base
    }

}
