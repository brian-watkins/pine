package org.pine

import org.junit.runners.model.FrameworkMethod
import org.pine.behavior.Behavior
import org.pine.visitor.SpecVisitor
import org.pine.block.*

trait Spec {

    SpecVisitor specVisitor

    List<Behavior> getBehaviors() {
        return specVisitor.getBehaviors()
    }

    Behavior findBehaviorWithName(String name) {
        return getBehaviors().find { b -> b.name == name }
    }

    FrameworkMethod getSpecMethod() {
        return specVisitor.getSpecMethod()
    }

    private void setDelegateForSpecClosure(Closure block) {
        if (specVisitor.specDelegate != null) {
            block.delegate = specVisitor.specDelegate
            block.resolveStrategy = Closure.OWNER_FIRST
        }
    }

    def describe(String name, Closure block) {
        println "Describe ${name}"
        specVisitor.visitRootContext(new ContextBlock(name))

        setDelegateForSpecClosure(block)

        block(this.&it)
    }

    def fit (String name, Closure block) {
        addBehavior(name, block, ExampleRunModifier.FOCUSED)
    }

    def xit (String name, Closure block) {
        addBehavior(name, block, ExampleRunModifier.IGNORED)
    }

    def it (String name, Closure block) {
        addBehavior(name, block, ExampleRunModifier.NONE)
    }

    def addBehavior (String name, Closure block, ExampleRunModifier runModifier) {
        System.out.println("it ${name}")

        setDelegateForSpecClosure(block)

        ExampleBlock node = new ExampleBlock()
        node.name = name
        node.block = block
        node.runModifier = runModifier

        specVisitor.visit(node)
    }

    def assume (Closure block) {
        System.out.println("assume")

        setDelegateForSpecClosure(block)

        specVisitor.visitAssumptionBlock(new ConfigurationBlock(block))
    }

    def when (String name, Closure block) {
        println "When ${name}"
        ContextBlock node = new ContextBlock(name)
        specVisitor.beginContext(node)

        setDelegateForSpecClosure(block)
        block(this.&it)

        specVisitor.endContext(node)
    }

    def clean (Closure block) {
        println "Clean"

        setDelegateForSpecClosure(block)

        specVisitor.visitCleanBlock(new ConfigurationBlock(block))
    }

}
