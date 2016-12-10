package org.pine.block

class ExampleBlock {
    Closure block
    String name
    ContextBlock context
    ExampleRunModifier runModifier

    boolean isIgnored () {
        return runModifier == ExampleRunModifier.IGNORED
    }

    boolean isFocused () {
        return runModifier == ExampleRunModifier.FOCUSED
    }

}
