package org.pine.visitor

import org.pine.behavior.Behavior

import org.pine.behavior.Feature
import org.pine.block.ContextBlock
import org.pine.block.ExampleBlock

import static org.pine.block.ExampleRunModifier.FOCUSED

class FeatureSpecVisitor extends AbstractSpecVisitor {

    private boolean hasFocusedBehaviors = false

    @Override
    void visit(ExampleBlock exampleBlock) {
        super.visit(exampleBlock)

        if (!hasFocusedBehaviors) {
            hasFocusedBehaviors = exampleBlock.getRunModifier() == FOCUSED
        }
    }

    @Override
    def List<Behavior> getBehaviors () {
        List<Behavior> features = new ArrayList<>()

        List<ExampleBlock> examples = getExamplesForContext(root)

        for (ExampleBlock example : examples) {
            Feature feature = new Feature(example)

            if (example.isIgnored() || (hasFocusedBehaviors && !example.isFocused())) {
                feature.setRunnable(false)
            }

            features.add(feature)
        }

        return features
    }

    def List<ExampleBlock> getExamplesForContext(ContextBlock context) {
        List<ExampleBlock> allExamples = (List<ExampleBlock>) context.examples

        context.children.each { c -> allExamples.addAll(getExamplesForContext(c)) }

        return allExamples
    }

}
