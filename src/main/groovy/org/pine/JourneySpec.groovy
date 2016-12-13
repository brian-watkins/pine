package org.pine

import org.pine.block.ExampleRunModifier

trait JourneySpec extends Spec {

    def she (String name, Closure block) {
        addBehavior(name, block, ExampleRunModifier.NONE)
    }

    def he (String name, Closure block) {
        addBehavior(name, block, ExampleRunModifier.NONE)
    }

}