package org.pine.statement

import org.junit.runners.model.Statement
import org.pine.block.ConfigurationBlock

class ConfigurationBlockStatement extends Statement {

    private Statement statement
    private List<ConfigurationBlock> blocks

    ConfigurationBlockStatement(List<ConfigurationBlock> blocks) {
        this(blocks, null)
    }

    ConfigurationBlockStatement (List<ConfigurationBlock> blocks, Statement statement) {
        this.statement = statement
        this.blocks = blocks
    }

    @Override
    void evaluate() throws Throwable {
        this.blocks.each { block ->
            block.block.call()
        }

        this.statement?.evaluate()
    }
}
