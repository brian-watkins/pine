package org.pine.statement

import org.junit.runners.model.Statement
import org.pine.SpecClass

import static org.pine.statement.SpecStatementHelper.setDelegateForSpecClosure

class FinalizerStatement extends Statement {

    private List<Closure> finalizers
    private SpecClass specClass

    public FinalizerStatement (SpecClass specClass, List<Closure> finalizers) {
        this.finalizers = finalizers
        this.specClass = specClass
    }

    @Override
    void evaluate() throws Throwable {
        this.finalizers.forEach { block ->
            println "Running finalizer"
            setDelegateForSpecClosure(specClass, block)
            block()
        }
    }
}
