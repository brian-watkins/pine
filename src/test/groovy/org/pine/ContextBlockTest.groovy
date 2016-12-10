package org.pine

import org.junit.Test
import org.pine.block.ContextBlock

class ContextBlockTest {

    @Test
    public void itGathersNamesFromAncestorsInOrder() {
        def rootContext = new ContextBlock()

        def parentContext = new ContextBlock()
        rootContext.addChild(parentContext)
        parentContext.name = "parent stuff"

        def childContext = new ContextBlock()
        childContext.name = "child stuff"
        parentContext.addChild(childContext)

        def grandChildContext = new ContextBlock()
        grandChildContext.name = "grandchild stuff"
        childContext.addChild(grandChildContext)

        def names = grandChildContext.collectNames()

        assert names.size() == 3
        assert names[0] == "parent stuff"
        assert names[1] == "child stuff"
        assert names[2] == "grandchild stuff"
    }
}
