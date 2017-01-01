package org.pine.visitor

import org.junit.runners.model.FrameworkMethod
import org.pine.Spec
import org.pine.annotation.Describe
import org.pine.annotation.SpecDelegate
import org.pine.behavior.Behavior
import org.pine.exception.SpecNotFoundException
import org.pine.util.SpecClass
import org.pine.block.ConfigurationBlock
import org.pine.block.ExampleBlock

import org.pine.block.ContextBlock

abstract class AbstractSpecVisitor implements SpecVisitor {

    protected ContextBlock root = new ContextBlock()
    protected ContextBlock currentContext = root
    private Object specDelegate = null
    private FrameworkMethod specMethod = null

    Object getSpecDelegate () {
        return specDelegate
    }

    FrameworkMethod getSpecMethod () {
        return specMethod
    }

    void prepare(SpecClass specClass, Spec specInstance) {
        specDelegate = findSpecDelegate(specClass, specInstance)
        specMethod = findSpecMethod(specClass, specInstance)
    }

    private Object findSpecDelegate(SpecClass specClass, Spec specInstance) {
        Object delegate = specClass.getAnnotatedFieldValues(specInstance, SpecDelegate, Object)
                .stream().findFirst().orElse(null)

        if (delegate == null) {
            println "AbstractSpecVisitor found NO delegate"
        } else {
            println "AbstractSpecVisitor found delegate: ${specDelegate.getClass().getName()}"
        }

        return delegate
    }

    private FrameworkMethod findSpecMethod(SpecClass specClass, Spec spec) {
        if (spec instanceof Script) {
            return new FrameworkMethod(spec.class.getMethod("run", null))
        }

        return specClass.getAnnotatedMethods(Describe).stream()
                .findFirst()
                .orElseThrow(SpecNotFoundException.metaClass.&invokeConstructor)
    }

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

    abstract List<Behavior> getBehaviors()
}
