package org.pine.visitor

import org.junit.runners.model.FrameworkMethod
import org.pine.Spec
import org.pine.behavior.Behavior
import org.pine.util.SpecClass
import org.pine.block.*

interface SpecVisitor {

    void beginContext(ContextBlock context)
    void endContext(ContextBlock context)
    void prepare(SpecClass spec, Spec specInstance)
    void visitRootContext(ContextBlock context)
    void visitAssumptionBlock(ConfigurationBlock assumptionBlock)
    void visit(ExampleBlock behaviorNode)
    void visitCleanBlock(ConfigurationBlock cleanBlock)

    List<Behavior> getBehaviors()
    FrameworkMethod getSpecMethod()
    Object getSpecDelegate()
}