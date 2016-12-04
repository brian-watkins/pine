package org.pine.statement

import org.junit.runners.model.Statement
import org.pine.Behavior
import org.pine.Spec
import org.pine.SpecClass

public class BehaviorStatement extends SpecStatement {

    private Statement statement
    private Behavior behavior

    public BehaviorStatement (SpecClass specClass, Spec spec, Behavior behavior, Statement statement) {
        super(specClass, spec)
        this.statement = statement
        this.behavior = behavior
    }

    @Override
    void evaluate() throws Throwable {
        println "Running behavior block"
        setDelegateForSpecClosure(behavior.block)

        behavior.block()

        this.statement.evaluate()
    }
}