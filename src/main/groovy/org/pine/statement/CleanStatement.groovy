package org.pine.statement

import org.pine.Spec
import org.pine.SpecClass

class CleanStatement extends SpecStatement {

    private List<Closure> cleaners

    public CleanStatement(SpecClass specClass, Spec specInstance, List<Closure> cleaners) {
        super(specClass, specInstance)
        this.cleaners = cleaners
    }

    @Override
    void evaluate() throws Throwable {
        this.cleaners.forEach { block ->
            println "Running cleaner"
            setDelegateForSpecClosure(block)
            block()
        }
    }
}
