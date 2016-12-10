package org.pine

import org.junit.Test
import org.pine.annotation.Describe
import org.pine.behavior.Behavior

import java.util.stream.Collectors

class WhenTest {

    class WhenSpec implements Spec {
        @Describe("when")
        def spec () {
            it "always works", {
                assert 1 == 1
            }

            when "we want to run a test", {
                it "understands basic arithmetic", {
                    assert 1 == 0
                }

                it "does fun stuff", {
                    assert 1 == 1
                }
            }

            when "we want to do something else", {
                it "still understands arithmetic", {
                    assert 1 == 1
                }

                when "something unexpected happens", {
                    it "responds appropriately", {
                        assert 1 == 1
                    }
                }
            }

            it "still works", {
                assert 1 == 1
            }
        }
    }

    @Test
    public void itNamesSpecsAccordingToTheirWhenBlock() {
        SpecRunner runner = new SpecRunner(WhenSpec)

        List<Behavior> behaviors = runner.getChildren()

        assert behaviors.size() == 6

        def behaviorNames = behaviors.stream().map({ b -> b.name }).collect(Collectors.toList())

        assert behaviorNames.contains("it always works")
        assert behaviorNames.contains("it still works")
        assert behaviorNames.contains("when we want to run a test, it understands basic arithmetic")
        assert behaviorNames.contains("when we want to run a test, it does fun stuff")
        assert behaviorNames.contains("when we want to do something else, it still understands arithmetic")
        assert behaviorNames.contains("when we want to do something else, and something unexpected happens, it responds appropriately")
    }
}
