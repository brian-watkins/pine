package org.pine

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
        addBehavior(name, block, true)
    }

    def it (String name, Closure block) {
        addBehavior(name, block, false)
    }

    private def addBehavior (String name, Closure block, boolean isFocused) {
        System.out.println("it ${name}")

        Behavior behavior = new Behavior()
        behavior.name = name
        behavior.block = block
        behavior.focused = isFocused

        currentBehaviorGroup.addBehavior(behavior)

        if (!hasFocusedBehaviors) {
            hasFocusedBehaviors = isFocused
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
