package org.pine.statement

import org.junit.runners.model.Statement
import org.pine.SpecClass

import static org.pine.statement.SpecStatementHelper.setDelegateForSpecClosure

class CleanStatement extends Statement {

    private List<Closure> cleaners
    private SpecClass specClass

    public CleanStatement(SpecClass specClass, List<Closure> cleaners) {
        this.cleaners = cleaners
        this.specClass = specClass
    }

    @Override
    void evaluate() throws Throwable {
        this.cleaners.forEach { block ->
            println "Running cleaner"
            setDelegateForSpecClosure(specClass, block)
            block()
        }
    }
}
