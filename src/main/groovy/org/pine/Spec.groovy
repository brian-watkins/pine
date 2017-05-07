package org.pine

import org.pine.visitor.SpecVisitor
import org.pine.block.*

trait Spec {

    SpecVisitor specVisitor

    def describe(String name, Closure block) {
        specVisitor.visitRootContext(new ContextBlock(name), block)

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
        ExampleBlock node = new ExampleBlock()
        node.name = name
        node.block = block
        node.runModifier = runModifier

        specVisitor.visit(node)
    }

    def assume (Closure block) {
        specVisitor.visitAssumptionBlock(new ConfigurationBlock(block))
    }

    def when (String name, Closure block) {
        ContextBlock node = new ContextBlock(name)
        specVisitor.beginContext(node, block)

        block(this.&it)

        specVisitor.endContext(node)
    }

    def clean (Closure block) {
        specVisitor.visitCleanBlock(new ConfigurationBlock(block))
    }

}
