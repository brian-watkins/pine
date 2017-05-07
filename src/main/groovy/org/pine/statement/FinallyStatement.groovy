package org.pine.statement

import org.junit.runners.model.Statement

class FinallyStatement extends Statement {

    private Statement tryStatement
    private Statement finallyStatement

    public FinallyStatement(Statement tryStatement, Statement finallyStatement) {
        this.tryStatement = tryStatement
        this.finallyStatement = finallyStatement
    }

    @Override
    void evaluate() throws Throwable {
        try {
            this.tryStatement.evaluate()
        }
        finally {
            this.finallyStatement.evaluate()
        }
    }
}
