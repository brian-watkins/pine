package org.pine.visitor

import org.pine.behavior.Behavior
import org.pine.util.SpecClass
import org.pine.block.ConfigurationBlock
import org.pine.block.ExampleBlock

import org.pine.block.ContextBlock

abstract class AbstractSpecVisitor implements SpecVisitor {

    protected ContextBlock root = new ContextBlock()
    protected ContextBlock currentContext = root

    @Override
    void visit(SpecClass specClass) { }

    @Override
    void visitRootContext(ContextBlock rootContext) { }

    @Override
    void beginContext(ContextBlock context) {
        currentContext.addChild(context)
        currentContext = context
    }

    @Override
    void endContext(ContextBlock context) {
        currentContext = currentContext.parent
    }

    @Override
    void visitAssumptionBlock(ConfigurationBlock assumption) {
        currentContext.addAssumption(assumption)
    }

    @Override
    void visit(ExampleBlock example) {
        currentContext.addExample(example)
    }

    @Override
    void visitCleanBlock(ConfigurationBlock block) {
        currentContext.addCleaner(block)
    }

    abstract List<Behavior> getBehaviors();
}
