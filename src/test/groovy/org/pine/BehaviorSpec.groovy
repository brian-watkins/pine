package org.pine

import org.junit.Test

class BehaviorSpec {

    @Test
    public void descriptionIsBasedOnParentGroup () {
        BehaviorGroup parent = new BehaviorGroup()
        parent.name = 'something happens'

        Behavior behavior = new Behavior()
        behavior.name = 'does something'

        parent.addBehavior(behavior)

        assert behavior.description().displayName == 'when something happens, it does something'
    }

    @Test
    public void whenGroupHasNoNameDescriptionContainsNoWhen () {
        BehaviorGroup parent = new BehaviorGroup()

        Behavior behavior = new Behavior()
        behavior.name = 'does something'

        parent.addBehavior(behavior)

        assert behavior.description().displayName == 'it does something'
    }

    @Test
    public void descriptionShowsAllAncestors() {
        BehaviorGroup parent = new BehaviorGroup()
        parent.name = 'something happens'

        BehaviorGroup child = new BehaviorGroup()
        child.name = 'another thing happens'
        parent.addSubGroup(child)

        BehaviorGroup grandChild = new BehaviorGroup()
        grandChild.name = 'things go awry'
        child.addSubGroup(grandChild)

        Behavior behavior = new Behavior()
        behavior.name = 'does something'

        grandChild.addBehavior(behavior)

        assert behavior.description().displayName == 'when something happens, and another thing happens, and things go awry, it does something'
    }

}
