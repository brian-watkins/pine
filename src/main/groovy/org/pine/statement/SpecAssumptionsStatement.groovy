package org.pine.statement

import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.pine.Spec
import org.pine.util.SpecClass
import org.pine.annotation.Assume

class SpecAssumptionsStatement extends SpecStatement {

    private Statement statement
    private List<FrameworkMethod> assumptionMethods

    public SpecAssumptionsStatement (SpecClass specClass, Spec specInstance, Statement statement) {
        super(specClass, specInstance)
        this.statement = statement
        this.assumptionMethods = specClass.getAnnotatedMethods(Assume)
    }

    @Override
    void evaluate() throws Throwable {
        this.assumptionMethods.each { assumptionMethod ->
            println "Running assumption method: ${assumptionMethod.name}"
            assumptionMethod.invokeExplosively(getSpecInstance())
        }

        this.statement.evaluate()
    }
}
