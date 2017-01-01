package org.pine.statement

import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.pine.Spec
import org.pine.util.SpecClass
import org.pine.annotation.Assume

class SpecAssumptionsStatement extends Statement {

    private Statement statement
    private List<FrameworkMethod> assumptionMethods
    private Spec specInstance

    SpecAssumptionsStatement (SpecClass specClass, Spec specInstance, Statement statement) {
        this.statement = statement
        this.specInstance = specInstance
        this.assumptionMethods = specClass.getAnnotatedMethods(Assume)
    }

    @Override
    void evaluate() throws Throwable {
        this.assumptionMethods.each { assumptionMethod ->
            println "Running assumption method: ${assumptionMethod.name}"
            assumptionMethod.invokeExplosively(specInstance)
        }

        this.statement.evaluate()
    }
}
