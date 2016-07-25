package org.pine

import org.junit.Test

/**
 * Created by bwatkins on 7/17/16.
 */
class BehaviorGroupTest {

    @Test
    public void itGathersAssumptionsFromAncestorsInOrder() {
        def closure_1 = { println 'yo' }
        def closure_2 = { println 'hey' }
        def closure_3 = { println 'why' }
        def closure_4 = { println 'huh' }

        def parentGroup = new BehaviorGroup()
        parentGroup.addAssumption(closure_1)
        parentGroup.addAssumption(closure_2)

        def childGroup = new BehaviorGroup()
        parentGroup.addSubGroup(childGroup)
        childGroup.addAssumption(closure_3)

        def grandChildGroup = new BehaviorGroup()
        childGroup.addSubGroup(grandChildGroup)
        grandChildGroup.addAssumption(closure_4)

        def assumptions = grandChildGroup.collectAssumptions()

        assert assumptions.size() == 4
        assert assumptions[0] == closure_1
        assert assumptions[1] == closure_2
        assert assumptions[2] == closure_3
        assert assumptions[3] == closure_4
    }

    @Test
    public void itGathersNamesFromAncestorsInOrder() {
        def rootGroup = new BehaviorGroup()

        def parentGroup = new BehaviorGroup()
        rootGroup.addSubGroup(parentGroup)
        parentGroup.name = "parent stuff"

        def childGroup = new BehaviorGroup()
        childGroup.name = "child stuff"
        parentGroup.addSubGroup(childGroup)

        def grandChildGroup = new BehaviorGroup()
        grandChildGroup.name = "grandchild stuff"
        childGroup.addSubGroup(grandChildGroup)

        def names = grandChildGroup.collectNames()

        assert names.size() == 3
        assert names[0] == "parent stuff"
        assert names[1] == "child stuff"
        assert names[2] == "grandchild stuff"
    }
}
