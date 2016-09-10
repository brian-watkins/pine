package org.pine.statement

import org.junit.runners.model.Statement
import org.pine.Behavior
import org.pine.SpecClass

import static org.pine.statement.SpecStatementHelper.setDelegateForSpecClosure

public class BehaviorStatement extends Statement {

    private Statement statement
    private Behavior behavior
    private SpecClass specClass

    public BehaviorStatement (SpecClass specClass, Behavior behavior, Statement statement) {
        this.statement = statement
        this.behavior = behavior
        this.specClass = specClass
    }

    @Override
    void evaluate() throws Throwable {
        println "Running behavior block"
        setDelegateForSpecClosure(specClass, behavior.block)

        behavior.block()

        this.statement.evaluate()
    }
}