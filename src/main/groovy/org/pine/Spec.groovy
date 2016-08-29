package org.pine

import static org.pine.BehaviorRunModifier.*

trait Spec {

    BehaviorGroup root = new BehaviorGroup()
    BehaviorGroup currentBehaviorGroup = root
    String specName
    boolean hasFocusedBehaviors = false

    def List<Behavior> getBehaviors () {
        return root.collectBehaviors()
    }

    def describe(String name, Closure block) {
        this.specName = name

        block(this.&it)
    }

    def fit (String name, Closure block) {
        addBehavior(name, block, FOCUSED)
    }

    def xit (String name, Closure block) {
        addBehavior(name, block, IGNORED)
    }

    def it (String name, Closure block) {
        addBehavior(name, block, NONE)
    }

    private def addBehavior (String name, Closure block, BehaviorRunModifier runModifier) {
        System.out.println("it ${name}")

        Behavior behavior = new Behavior()
        behavior.name = name
        behavior.block = block
        behavior.runModifier = runModifier

        currentBehaviorGroup.addBehavior(behavior)

        if (!hasFocusedBehaviors) {
            hasFocusedBehaviors = runModifier == FOCUSED
        }
    }

    def assume (Closure block) {
        System.out.println("assume")
        currentBehaviorGroup.addAssumption(block)
    }

    def when (String name, Closure block) {
        println "When ${name}"
        BehaviorGroup behaviorGroup = new BehaviorGroup()
        behaviorGroup.name = name
        currentBehaviorGroup.addSubGroup(behaviorGroup)
        currentBehaviorGroup = behaviorGroup

        block(this.&it)

        currentBehaviorGroup = behaviorGroup.superGroup
    }

    def finalize (Closure block) {
        println "Finalizer"
        currentBehaviorGroup.addFinalizer(block)
    }

}
