package org.pine.visitor

import org.pine.behavior.Behavior
import org.pine.util.SpecClass
import org.pine.block.*

interface SpecVisitor {

    public void beginContext(ContextBlock context)
    public void endContext(ContextBlock context)
    public void visit(SpecClass spec)
    public void visitRootContext(ContextBlock context)
    public void visitAssumptionBlock(ConfigurationBlock assumptionBlock)
    public void visit(ExampleBlock behaviorNode)
    public void visitCleanBlock(ConfigurationBlock cleanBlock)

    public List<Behavior> getBehaviors()
}