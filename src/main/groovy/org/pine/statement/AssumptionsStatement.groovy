package org.pine.statement

import org.junit.runners.model.Statement
import org.pine.Spec
import org.pine.util.SpecClass
import org.pine.block.ConfigurationBlock

class AssumptionsStatement extends SpecStatement {

    private Statement statement
    private List<ConfigurationBlock> assumptions

    public AssumptionsStatement (SpecClass specClass, Spec specInstance, List<ConfigurationBlock> assumptions, Statement statement) {
        super(specClass, specInstance)
        this.statement = statement
        this.assumptions = assumptions
    }

    @Override
    void evaluate() throws Throwable {
        this.assumptions.each { assumption ->
            println "Running assumption block"
            setDelegateForSpecClosure(assumption.block)
            assumption.block()
        }

        this.statement.evaluate()
    }
}
