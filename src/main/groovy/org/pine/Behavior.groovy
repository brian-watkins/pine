package org.pine

import static org.pine.BehaviorRunModifier.*

class Behavior {
    Closure block
    String name
    BehaviorGroup group
    BehaviorRunModifier runModifier

    def List<Closure> getAssumptions() {
        group.collectAssumptions()
    }

    def List<Closure> getFinalizers() {
        group.collectFinalizers()
    }

    def String getDisplayName() {
        getGroupDescription() + "it ${name}"
    }

    def boolean isIgnored () {
        return runModifier == IGNORED
    }

    def boolean isFocused () {
        return runModifier == FOCUSED
    }

    private def getGroupDescription() {
        def groupNames = group.collectNames()
        if (groupNames.size() > 0) {
            return "when ${groupNames.join(", and ")}, "
        }
        return ""
    }
}
