package org.pine.behavior

import org.junit.runners.model.Statement
import org.pine.block.ConfigurationBlock
import org.pine.statement.ConfigurationBlockStatement
import org.pine.block.ContextBlock
import org.pine.block.ExampleBlock

import org.pine.statement.BehaviorStatement

class Journey implements Behavior {

    List<String> contextNames
    ContextBlock rootContext

    Journey (List<String> contextNames, ContextBlock rootContext) {
        this.contextNames = contextNames
        this.rootContext = rootContext
    }

    String getName() {
        if (contextNames.size() > 1) {
            return rootContext.getName() +
                    ", when " + contextNames.subList(1, contextNames.size()).join(", and ")
        }

        return rootContext.getName()
    }

    boolean shouldRun() {
        return true
    }

    private List<ConfigurationBlock> getCleaners() {
        return getConfigurationBlocks { context -> context.cleaners }
    }

    private List<ConfigurationBlock> getAssumptions() {
        return getConfigurationBlocks { context -> context.assumptions }
    }

    private List<ConfigurationBlock> getConfigurationBlocks(Closure block) {
        List<ConfigurationBlock> blocks = new ArrayList<>()

        for (String name : contextNames) {
            ContextBlock g = findContextWithName(this.rootContext, name)
            blocks.addAll(block(g))
        }

        return blocks
    }

    Statement createStatement() {
        Statement runStatement = new ConfigurationBlockStatement(this.cleaners)

        for (String name : contextNames.reverse()) {
            ContextBlock g = findContextWithName(this.rootContext, name)
            runStatement = new BehaviorStatement(getBlockForContext(g), runStatement)
        }

        runStatement = new ConfigurationBlockStatement(this.assumptions, runStatement)

        return runStatement
    }

    private ContextBlock findContextWithName(ContextBlock context, String name) {
        if (context.name == name) {
            return context
        }

        for (ContextBlock childContext : context.children) {
            ContextBlock foundContext = findContextWithName(childContext, name)
            if (foundContext != null) {
                return foundContext
            }
        }

        return null;
    }

    private Closure getBlockForContext(ContextBlock contextBlock) {
        def journey = {

            contextBlock.examples.each({ ExampleBlock example ->
                example.block.delegate = owner
                example.block.resolveStrategy = DELEGATE_FIRST
                example.block()
            })

        }

        return journey
    }
}
