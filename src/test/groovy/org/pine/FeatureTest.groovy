package org.pine

import org.junit.Test
import org.pine.behavior.Feature
import org.pine.block.ConfigurationBlock
import org.pine.block.ContextBlock
import org.pine.block.ExampleBlock

class FeatureTest {

    @Test
    public void descriptionIsBasedOnParentGroup () {
        ContextBlock parent = new ContextBlock('something happens')

        ExampleBlock example = new ExampleBlock()
        example.name = "does something"
        example.context = parent

        Feature feature = new Feature(example)

        assert feature.name == 'when something happens, it does something'
    }

    @Test
    public void whenGroupHasNoNameDescriptionContainsNoWhen () {
        ContextBlock parent = new ContextBlock(null)

        ExampleBlock example = new ExampleBlock()
        example.name = "does something"
        example.context = parent

        Feature feature = new Feature(example)

        assert feature.name == 'it does something'
    }

    @Test
    public void itGathersAssumptionsFromAncestorsInOrder() {
        def closure_1 = new ConfigurationBlock({ println 'yo' })
        def closure_2 = new ConfigurationBlock({ println 'hey' })
        def closure_3 = new ConfigurationBlock({ println 'why' })
        def closure_4 = new ConfigurationBlock({ println 'huh' })

        def parentGroup = new ContextBlock("parent")
        parentGroup.addAssumption(closure_1)
        parentGroup.addAssumption(closure_2)

        def childGroup = new ContextBlock("child")
        parentGroup.addChild(childGroup)
        childGroup.addAssumption(closure_3)

        def grandChildGroup = new ContextBlock("grandchild")
        childGroup.addChild(grandChildGroup)
        grandChildGroup.addAssumption(closure_4)

        ExampleBlock example = new ExampleBlock()
        example.context = grandChildGroup

        Feature feature = new Feature(example);
        def assumptions = feature.getAssumptions()

        assert assumptions.size() == 4
        assert assumptions[0] == closure_1
        assert assumptions[1] == closure_2
        assert assumptions[2] == closure_3
        assert assumptions[3] == closure_4
    }

    @Test
    public void descriptionShowsAllAncestors() {
        ContextBlock parent = new ContextBlock('something happens')

        ContextBlock child = new ContextBlock('another thing happens')
        parent.addChild(child)

        ContextBlock grandChild = new ContextBlock('things go awry')
        child.addChild(grandChild)

        ExampleBlock example = new ExampleBlock()
        example.name = 'does something'
        example.context = grandChild

        Feature feature = new Feature(example)

        assert feature.name == 'when something happens, and another thing happens, and things go awry, it does something'
    }

}
