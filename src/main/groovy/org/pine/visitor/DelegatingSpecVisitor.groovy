package org.pine.visitor

import org.pine.Spec
import org.pine.annotation.SpecDelegate
import org.pine.block.ConfigurationBlock
import org.pine.block.ContextBlock
import org.pine.block.ExampleBlock
import org.pine.util.SpecClass

abstract class DelegatingSpecVisitor extends AbstractSpecVisitor {

    private Object specDelegate = null

    @Override
    void prepare(SpecClass specClass, Spec specInstance) {
        super.prepare(specClass, specInstance)
        specDelegate = findSpecDelegate(specClass, specInstance)
    }

    @Override
    void visitRootContext(ContextBlock rootContext, Closure block) {
        super.visitRootContext(rootContext, block)
        setDelegateForSpecClosure(block)
    }

    @Override
    void beginContext(ContextBlock context, Closure block) {
        super.beginContext(context, block)
        setDelegateForSpecClosure(block)
    }

    @Override
    void visitAssumptionBlock(ConfigurationBlock assumption) {
        super.visitAssumptionBlock(assumption)
        setDelegateForSpecClosure(assumption.block)
    }

    @Override
    void visit(ExampleBlock example) {
        super.visit(example)
        setDelegateForSpecClosure(example.block)
    }

    @Override
    void visitCleanBlock(ConfigurationBlock cleaner) {
        super.visitCleanBlock(cleaner)
        setDelegateForSpecClosure(cleaner.block)
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

    private void setDelegateForSpecClosure(Closure block) {
        if (specDelegate != null) {
            block.delegate = specDelegate
            block.resolveStrategy = Closure.OWNER_FIRST
        }
    }

}
