package org.pine

trait Spec {

    BehaviorGroup root = new BehaviorGroup()
    BehaviorGroup currentBehaviorGroup = root
    String specName

    def List<Behavior> getBehaviors () {
        return root.collectBehaviors()
    }

    def describe(name, block) {
        this.specName = name

        block(this.&it)
    }

    def it (name, block) {
        System.out.println("it ${name}")
        Behavior behavior = new Behavior()
        behavior.name = name
        behavior.block = block

        currentBehaviorGroup.addBehavior(behavior)
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
