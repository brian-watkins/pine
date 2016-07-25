package org.pine.testHelpers

import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement

class FunMethodRule implements MethodRule {

    public String methodName

    @Override
    Statement apply(Statement base, FrameworkMethod method, Object target) {
        println "Applying the method rule! I am: ${this}"

        println "Got a method: ${method.getMethod().getName()}"

        methodName = method.getMethod().getName()

        return base
    }
}
