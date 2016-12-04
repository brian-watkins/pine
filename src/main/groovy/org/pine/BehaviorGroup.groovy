package org.pine

class BehaviorGroup {
    def name
    def superGroup

    private List<Closure> assumptions = []
    private List<Behavior> behaviors = []
    private List<Closure> cleaners = []
    private List<BehaviorGroup> subGroups = []

    def addAssumption (Closure c) {
        assumptions << c
    }

    def addCleaner (Closure c) {
        cleaners << c
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

    def List<Closure> collectCleaners() {
        def allCleaners = []

        allCleaners.addAll(this.cleaners)
        allCleaners.addAll(superGroup?.collectCleaners() ?: [])

        return allCleaners
    }

    def List<String> collectNames() {
        def names = superGroup?.collectNames() ?: []
        if (this.name) names << this.name
        return names
    }
}
