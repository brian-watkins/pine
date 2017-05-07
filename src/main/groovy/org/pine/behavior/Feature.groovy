package org.pine.behavior

import org.junit.runners.model.Statement
import org.pine.statement.ConfigurationBlockStatement
import org.pine.block.ConfigurationBlock
import org.pine.block.ContextBlock
import org.pine.block.ExampleBlock

import org.pine.statement.BehaviorStatement
import org.pine.statement.FinallyStatement

class Feature implements Behavior {

    ExampleBlock example
    boolean runnable = true

    Feature (ExampleBlock example) {
        this.example = example
    }

    boolean shouldRun() {
        return this.runnable
    }

    List<ConfigurationBlock> getAssumptions() {
        gatherAssumptions(example.context)
    }

    private List<ConfigurationBlock> gatherAssumptions(ContextBlock context) {
        def assumptions = []
        if (context.parent != null) {
            assumptions.addAll(gatherAssumptions(context.parent))
        }

        assumptions.addAll(context.assumptions)

        return assumptions
    }

    private List<ConfigurationBlock> getCleaners() {
        gatherCleaners(example.context)
    }

    private List<ConfigurationBlock> gatherCleaners(ContextBlock context) {
        def allCleaners = []

        allCleaners.addAll(context.cleaners)
        if (context.parent != null) {
            allCleaners.addAll(gatherCleaners(context.parent))
        }

        return allCleaners
    }

    String getName() {
        getGroupDescription() + "it ${example.name}"
    }

    private String getGroupDescription() {
        def groupNames = example.context.collectNames()
        if (groupNames.size() > 0) {
            return "when ${groupNames.join(", and ")}, "
        }
        return ""
    }

    Statement createStatement() {
        Statement cleanStatement = new ConfigurationBlockStatement(this.cleaners)
        Statement behaviorStatement = new BehaviorStatement(example.block)

        Statement runStatement = new FinallyStatement(behaviorStatement, cleanStatement)
        runStatement = new ConfigurationBlockStatement(getAssumptions(), runStatement)
        return runStatement
    }
}
