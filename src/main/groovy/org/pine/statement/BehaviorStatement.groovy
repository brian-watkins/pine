package org.pine.statement

import org.junit.runners.model.Statement
import org.pine.Spec
import org.pine.util.SpecClass

public class BehaviorStatement extends SpecStatement {

    private Statement statement
    private Closure block

    public BehaviorStatement (SpecClass specClass, Spec spec, Closure block, Statement statement) {
        super(specClass, spec)
        this.statement = statement
        this.block = block
    }

    @Override
    void evaluate() throws Throwable {
        println "Running behavior block"
        setDelegateForSpecClosure(this.block)

        this.block()

        this.statement.evaluate()
    }
}