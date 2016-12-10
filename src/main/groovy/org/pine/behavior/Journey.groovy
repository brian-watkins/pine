package org.pine.behavior

import jdk.nashorn.internal.runtime.regexp.joni.Config
import org.junit.runners.model.Statement
import org.pine.Spec
import org.pine.block.ConfigurationBlock
import org.pine.util.SpecClass
import org.pine.block.ContextBlock
import org.pine.block.ExampleBlock
import org.pine.statement.AssumptionsStatement
import org.pine.statement.BehaviorStatement
import org.pine.statement.CleanStatement

class Journey implements Behavior {

    List<String> contextNames
    ContextBlock rootContext

    public Journey (List<String> contextNames, ContextBlock rootContext) {
        this.contextNames = contextNames
        this.rootContext = rootContext
    }

    public String getName() {
        if (contextNames.size() > 1) {
            return rootContext.getName() +
                    ", when " + contextNames.subList(1, contextNames.size()).join(", and ")
        }

        return rootContext.getName()
    }

    public boolean shouldRun() {
        return true
    }

    private List<ConfigurationBlock> getCleaners() {
        List<ConfigurationBlock> cleaners = new ArrayList<>()

        for (String name : contextNames) {
            ContextBlock g = findContextWithName(this.rootContext, name)
            cleaners.addAll(g.cleaners)
        }

        return cleaners
    }


    public Statement createStatement(SpecClass specClass, Spec specInstance) {
        Statement runStatement = new CleanStatement(specClass, specInstance, this.cleaners)

        for (String name : contextNames.reverse()) {
            ContextBlock g = findContextWithName(this.rootContext, name)

            runStatement = new BehaviorStatement(specClass, specInstance, getBlockForContext(g), runStatement)
            runStatement = new AssumptionsStatement(specClass, specInstance, g.assumptions, runStatement)
        }

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
