package org.pine.statement

import org.junit.runners.model.Statement
import org.pine.Behavior
import org.pine.Spec
import org.pine.SpecClass
import org.pine.annotation.SpecDelegate

import static org.pine.statement.SpecStatementHelper.setDelegateForSpecClosure

public class BehaviorStatement extends Statement {

    private Statement statement
    private Behavior behavior
    private SpecClass specClass
    private Spec specInstance

    public BehaviorStatement (SpecClass specClass, Spec spec, Behavior behavior, Statement statement) {
        this.statement = statement
        this.behavior = behavior
        this.specClass = specClass
        this.specInstance = spec
    }

    @Override
    void evaluate() throws Throwable {
        println "Running behavior block"
        setDelegateForSpecClosure(specClass, specInstance, behavior.block)

        behavior.block()

        this.statement.evaluate()
    }
}