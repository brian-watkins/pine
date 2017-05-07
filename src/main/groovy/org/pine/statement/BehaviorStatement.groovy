package org.pine.statement

import org.junit.runners.model.Statement

class BehaviorStatement extends Statement {

    private Statement statement
    private Closure block

    BehaviorStatement (Closure block, Statement statement) {
        this.statement = statement
        this.block = block
    }

    BehaviorStatement (Closure block) {
        this(block, null)
    }

    @Override
    void evaluate() throws Throwable {
        this.block()

        this.statement?.evaluate()
    }
}