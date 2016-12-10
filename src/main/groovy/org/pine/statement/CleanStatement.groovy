package org.pine.statement

import org.pine.Spec
import org.pine.util.SpecClass
import org.pine.block.ConfigurationBlock

class CleanStatement extends SpecStatement {

    private List<ConfigurationBlock> cleaners

    public CleanStatement(SpecClass specClass, Spec specInstance, List<ConfigurationBlock> cleaners) {
        super(specClass, specInstance)
        this.cleaners = cleaners
    }

    @Override
    void evaluate() throws Throwable {
        this.cleaners.forEach { cleaner ->
            println "Running cleaner"
            setDelegateForSpecClosure(cleaner.block)
            cleaner.block()
        }
    }
}
