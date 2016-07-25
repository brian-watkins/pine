package org.pine

import org.junit.runner.Description

class Behavior {
    def block
    def name
    def group

    def List<Closure> getAssumptions() {
        return group.collectAssumptions()
    }

    def description () {
        new Description(null, getDisplayName())
    }

    private def getDisplayName() {
        return getGroupDescription() + "it ${name}"
    }

    private def getGroupDescription() {
        def groupNames = group.collectNames()
        if (groupNames.size() > 0) {
            return "when ${groupNames.join(", and ")}, "
        }
        return ""
    }
}
