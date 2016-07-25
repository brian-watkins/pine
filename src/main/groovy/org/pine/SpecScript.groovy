package org.pine

import org.junit.runner.RunWith

@RunWith(FunSpecRunner.class)
abstract class SpecScript extends Script {

    def root = new BehaviorGroup()
    def currentBehaviorGroup = root

    abstract def gatherSpecs()

    def List<Behavior> getBehaviors () {
        return root.collectBehaviors()
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

    def run() {
        println "This is a spec. Run it with JUnit, please."
    }

}
