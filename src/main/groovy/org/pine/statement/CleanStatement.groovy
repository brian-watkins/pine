package org.pine.statement

import org.junit.runners.model.Statement
import org.pine.Spec
import org.pine.SpecClass

import static org.pine.statement.SpecStatementHelper.setDelegateForSpecClosure

class CleanStatement extends Statement {

    private List<Closure> cleaners
    private SpecClass specClass
    private Spec specInstance

    public CleanStatement(SpecClass specClass, Spec specInstance, List<Closure> cleaners) {
        this.cleaners = cleaners
        this.specClass = specClass
        this.specInstance = specInstance
    }

    @Override
    void evaluate() throws Throwable {
        this.cleaners.forEach { block ->
            println "Running cleaner"
            setDelegateForSpecClosure(specClass, specInstance, block)
            block()
        }
    }
}
