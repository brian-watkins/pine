package org.pine.visitor

import org.pine.Spec
import org.pine.util.SpecClass
import org.pine.annotation.Describe
import org.pine.behavior.Behavior

import org.pine.behavior.Journey
import org.pine.block.ContextBlock

class JourneySpecVisitor extends DelegatingSpecVisitor {

    @Override
    void prepare(SpecClass specClass, Spec specInstance) {
        super.prepare(specClass, specInstance)

        String specName = specMethod?.getAnnotation(Describe.class)?.value() ?: specClass.specClass.name
        root.setName(specName)
    }

    @Override
    void visitRootContext(ContextBlock rootContext, Closure block) {
        super.visitRootContext(rootContext, block)
        this.root = rootContext
        this.currentContext = rootContext
    }

    @Override
    List<Behavior> getBehaviors () {
        List<Journey> journeys = new ArrayList<>()
        for (ContextBlock block : getLeaves(root)) {
            journeys.add(new Journey(block.collectNames(), root))
        }

        return journeys
    }

    private List<ContextBlock> getLeaves(ContextBlock contextBlock) {
        List<ContextBlock> leaves = new ArrayList<>()

        if (!contextBlock.hasChildren()) {
            leaves.add(contextBlock)
            return leaves
        }

        for (ContextBlock childBlock : contextBlock.children) {
            leaves.addAll(getLeaves(childBlock))
        }

        return leaves
    }

}
