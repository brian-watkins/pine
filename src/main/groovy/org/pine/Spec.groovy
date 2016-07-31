package org.pine

trait Spec {

    BehaviorGroup root = new BehaviorGroup()
    BehaviorGroup currentBehaviorGroup = root
    String specName
    boolean hasFocusedBehaviors = false

    def List<Behavior> getBehaviors () {
        return root.collectBehaviors()
    }

    def describe(name, block) {
        this.specName = name

        block(this.&it)
    }

    def fit (name, block) {
        addBehavior(name, block, true)
    }

    def it (name, block) {
        addBehavior(name, block, false)
    }

    private def addBehavior (name, block, isFocused) {
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

    def assume (block) {
        System.out.println("assume")
        currentBehaviorGroup.assumptions << block
    }

    def when (name, block) {
        BehaviorGroup behaviorGroup = new BehaviorGroup()
        behaviorGroup.name = name
        currentBehaviorGroup.addSubGroup(behaviorGroup)
        currentBehaviorGroup = behaviorGroup

        block(this.&it)

        currentBehaviorGroup = behaviorGroup.superGroup
    }

}
