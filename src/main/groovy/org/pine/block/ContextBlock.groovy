package org.pine.block

class ContextBlock {
    String name
    ContextBlock parent
    List<ConfigurationBlock> assumptions = []
    List<ExampleBlock> examples = []
    List<ConfigurationBlock> cleaners = []
    List<ContextBlock> children = []

    ContextBlock() {
        this.name = null
    }

    ContextBlock(String name) {
        this.name = name
    }

    def addAssumption (ConfigurationBlock block) {
        assumptions << block
    }

    def addCleaner (ConfigurationBlock block) {
        cleaners << block
    }

    def addExample (ExampleBlock example) {
        example.context = this
        examples << example
    }

    def addChild(ContextBlock contextBlock) {
        contextBlock.parent = this
        children << contextBlock
    }

    def hasChildren() {
        return children.size() > 0
    }

    List<String> collectNames() {
        List<String> names = parent?.collectNames() ?: new ArrayList<String>()
        if (this.name) names << this.name
        return names
    }
}
