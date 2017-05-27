package org.pine.visitor

import org.junit.runners.model.FrameworkMethod
import org.pine.Spec
import org.pine.annotation.Describe
import org.pine.behavior.Behavior
import org.pine.exception.SpecNotFoundException
import org.pine.util.SpecClass
import org.pine.block.ConfigurationBlock
import org.pine.block.ExampleBlock

import org.pine.block.ContextBlock

abstract class AbstractSpecVisitor implements SpecVisitor {

    protected ContextBlock root = new ContextBlock()
    protected ContextBlock currentContext = root
    private FrameworkMethod specMethod = null

    FrameworkMethod getSpecMethod () {
        return specMethod
    }

    void prepare(SpecClass specClass, Spec specInstance) {
        specMethod = findSpecMethod(specClass, specInstance)
    }

    private FrameworkMethod findSpecMethod(SpecClass specClass, Spec spec) {
        if (spec instanceof Script) {
            return new FrameworkMethod(spec.class.getMethod("run", null))
        }

        return specClass.getAnnotatedMethods(Describe).stream()
                .findFirst()
                .orElseThrow({ new SpecNotFoundException() })
    }

    @Override
    void visitRootContext(ContextBlock rootContext, Closure block) {
    }

    @Override
    void beginContext(ContextBlock context, Closure block) {
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
    void visitCleanBlock(ConfigurationBlock cleaner) {
        currentContext.addCleaner(cleaner)
    }

    abstract List<Behavior> getBehaviors()

    Behavior getBehaviorWithName(String name) {
        return getBehaviors().find { b -> b.name == name }
    }
}
