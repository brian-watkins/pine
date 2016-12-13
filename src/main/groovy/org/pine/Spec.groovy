package org.pine

import org.pine.behavior.Behavior
import org.pine.visitor.SpecVisitor
import org.pine.block.*

trait Spec {

    SpecVisitor specVisitor

    public List<Behavior> getBehaviors() {
        return specVisitor.getBehaviors()
    }

    def describe(String name, Closure block) {
        println "Describe ${name}"
        specVisitor.visitRootContext(new ContextBlock(name))
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

        ExampleBlock node = new ExampleBlock()
        node.name = name
        node.block = block
        node.runModifier = runModifier

        specVisitor.visit(node)
    }

    def assume (Closure block) {
        System.out.println("assume")
        specVisitor.visitAssumptionBlock(new ConfigurationBlock(block))
    }

    def when (String name, Closure block) {
        println "When ${name}"
        ContextBlock node = new ContextBlock(name)
        specVisitor.beginContext(node)

        block(this.&it)

        specVisitor.endContext(node)
    }

    def clean (Closure block) {
        println "Clean"
        specVisitor.visitCleanBlock(new ConfigurationBlock(block))
    }

}
