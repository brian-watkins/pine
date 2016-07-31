package org.pine

import org.junit.runner.Description

class Behavior {
    Closure block
    String name
    BehaviorGroup group
    boolean focused = false

    def List<Closure> getAssumptions() {
        group.collectAssumptions()
    }

    def String getDisplayName() {
        getGroupDescription() + "it ${name}"
    }

    private def getGroupDescription() {
        def groupNames = group.collectNames()
        if (groupNames.size() > 0) {
            return "when ${groupNames.join(", and ")}, "
        }
        return ""
    }
}
