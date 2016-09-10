package org.pine.statement

import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.pine.Spec
import org.pine.SpecClass
import org.pine.annotation.Assume

import static org.pine.statement.SpecStatementHelper.setDelegateForSpecClosure

class AssumptionsStatement extends Statement {

    private Statement statement
    private List<Closure> assumptions
    private List<FrameworkMethod> assumptionMethods
    private Spec spec
    private SpecClass specClass

    public AssumptionsStatement (SpecClass specClass, Spec specInstance, List<Closure> assumptions, Statement statement) {
        this.statement = statement
        this.assumptions = assumptions
        this.spec = specInstance
        this.assumptionMethods = specClass.getAnnotatedMethods(Assume)
        this.specClass = specClass
    }

    @Override
    void evaluate() throws Throwable {
        this.assumptionMethods.each { assumptionMethod ->
            println "Running assumption method: ${assumptionMethod.name}"
            assumptionMethod.invokeExplosively(spec)
        }

        this.assumptions.each { assumption ->
            println "Running assumption block"
            setDelegateForSpecClosure(specClass, assumption)
            assumption()
        }

        this.statement.evaluate()
    }
}
