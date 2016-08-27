package org.pine

class BehaviorGroup {
    def name
    def superGroup;

    private List<Closure> assumptions = []
    private List<Behavior> behaviors = []
    private List<Closure> finalizers = []
    private List<BehaviorGroup> subGroups = []

    def addAssumption (Closure c) {
        assumptions << c
    }

    def addFinalizer (Closure c) {
        finalizers << c
    }

    def addBehavior (Behavior behavior) {
        behavior.group = this
        behaviors << behavior
    }

    def addSubGroup (BehaviorGroup behaviorGroup) {
        behaviorGroup.superGroup = this
        subGroups << behaviorGroup
    }

    def List<Behavior> collectBehaviors() {
        def behaviors = this.behaviors

        subGroups.each { bg -> behaviors.addAll(bg.collectBehaviors()) }

        return behaviors
    }

    def List<Closure> collectAssumptions() {
        def assumptions = superGroup?.collectAssumptions() ?: []
        assumptions.addAll(this.assumptions)
        return assumptions
    }

    def List<Closure> collectFinalizers() {
        def allFinalizers = []

        allFinalizers.addAll(this.finalizers)
        allFinalizers.addAll(superGroup?.collectFinalizers() ?: [])

        return allFinalizers
    }

    def List<String> collectNames() {
        def names = superGroup?.collectNames() ?: []
        if (this.name) names << this.name
        return names
    }
}
