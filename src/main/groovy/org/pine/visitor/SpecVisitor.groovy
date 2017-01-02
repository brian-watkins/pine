package org.pine.visitor

import org.junit.runners.model.FrameworkMethod
import org.pine.Spec
import org.pine.behavior.Behavior
import org.pine.util.SpecClass
import org.pine.block.*

interface SpecVisitor {

    void prepare(SpecClass spec, Spec specInstance)
    void visitRootContext(ContextBlock context, Closure block)
    void beginContext(ContextBlock context, Closure block)
    void endContext(ContextBlock context)
    void visitAssumptionBlock(ConfigurationBlock assumptionBlock)
    void visit(ExampleBlock behaviorNode)
    void visitCleanBlock(ConfigurationBlock cleanBlock)

    List<Behavior> getBehaviors()
    FrameworkMethod getSpecMethod()
    Behavior getBehaviorWithName(String name)
}