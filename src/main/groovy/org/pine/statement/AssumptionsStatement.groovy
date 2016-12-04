package org.pine.statement

import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.pine.Spec
import org.pine.SpecClass
import org.pine.annotation.Assume

class AssumptionsStatement extends SpecStatement {

    private Statement statement
    private List<Closure> assumptions
    private List<FrameworkMethod> assumptionMethods

    public AssumptionsStatement (SpecClass specClass, Spec specInstance, List<Closure> assumptions, Statement statement) {
        super(specClass, specInstance)
        this.statement = statement
        this.assumptions = assumptions
        this.assumptionMethods = specClass.getAnnotatedMethods(Assume)
    }

    @Override
    void evaluate() throws Throwable {
        this.assumptionMethods.each { assumptionMethod ->
            println "Running assumption method: ${assumptionMethod.name}"
            assumptionMethod.invokeExplosively(getSpecInstance())
        }

        this.assumptions.each { assumption ->
            println "Running assumption block"
            setDelegateForSpecClosure(assumption)
            assumption()
        }

        this.statement.evaluate()
    }
}
