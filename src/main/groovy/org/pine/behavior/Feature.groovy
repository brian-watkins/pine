package org.pine.behavior

import org.junit.runners.model.Statement
import org.pine.Spec
import org.pine.util.SpecClass
import org.pine.block.ConfigurationBlock
import org.pine.block.ContextBlock
import org.pine.block.ExampleBlock
import org.pine.statement.AssumptionsStatement
import org.pine.statement.BehaviorStatement
import org.pine.statement.CleanStatement

class Feature implements Behavior {

    ExampleBlock example
    boolean runnable = true

    public Feature (ExampleBlock example) {
        this.example = example
    }

    public boolean shouldRun() {
        return this.runnable
    }

    public List<ConfigurationBlock> getAssumptions() {
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

    public String getName() {
        getGroupDescription() + "it ${example.name}"
    }

    private String getGroupDescription() {
        def groupNames = example.context.collectNames()
        if (groupNames.size() > 0) {
            return "when ${groupNames.join(", and ")}, "
        }
        return ""
    }

    Statement createStatement(SpecClass specClass, Spec specInstance) {
        Statement runStatement = new CleanStatement(specClass, specInstance, this.cleaners)
        runStatement = new BehaviorStatement(specClass, specInstance, example.block, runStatement)
        runStatement = new AssumptionsStatement(specClass, specInstance, getAssumptions(), runStatement)
        return runStatement
    }
}
