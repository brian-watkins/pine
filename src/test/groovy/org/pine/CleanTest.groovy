package org.pine

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runner.notification.RunNotifier
import org.pine.annotation.Describe
import org.pine.annotation.SpecDelegate
import org.pine.testHelpers.SpecTestRunListener

import static org.pine.testHelpers.TestHelper.assertBehaviorPasses
import static org.pine.testHelpers.TestHelper.assertSpecRuns

class CleanTest {

    static class OrderRecorder {
        static def order = new ArrayList<String>()
    }

    static class CleanSpec implements Spec {

        int someNumber = 0

        @Describe("Clean Block Spec")
        def spec() {

            clean {
                OrderRecorder.order.add("clean 1")
                someNumber = 1
            }

            clean {
                OrderRecorder.order.add("clean 2")
            }

            it 'runs a spec', {
                OrderRecorder.order.add("spec")
                assert someNumber == 0
            }

            it 'does other things', {
                OrderRecorder.order.add("other spec")
                assert 2 == 2
            }

        }
    }

    @Before
    void setUp() {
        OrderRecorder.order.clear()
    }

    @Test
    public void itRunsTheCleanBlocksInOrderAfterABehavior () {
        SpecRunner runner = new SpecRunner(CleanSpec)
        assertBehaviorPasses(runner, runner.getChildren()[0])

        assert OrderRecorder.order == [ 'spec', 'clean 1', 'clean 2' ]
    }

    @Test
    public void itRunsTheCleanBlocksAfterEachBehavior () {
        SpecRunner runner = new SpecRunner(CleanSpec)
        assertBehaviorPasses(runner, runner.getChildren()[0])
        assertBehaviorPasses(runner, runner.getChildren()[1])

        assert OrderRecorder.order == [ 'spec', 'clean 1', 'clean 2', 'other spec', 'clean 1', 'clean 2' ]
    }

    static class CleanContextsSpec implements Spec {

        int someNumber = 0

        @Describe("Clean Block Spec")
        def spec() {

            when 'things are the case', {

                clean {
                    OrderRecorder.order.add("clean 1")
                }

                it 'runs a spec', {
                    OrderRecorder.order.add("spec")
                    assert someNumber == 0
                }

            }

            when 'things are not the case', {

                clean {
                    OrderRecorder.order.add("clean 2")
                }

                it 'does other things', {
                    OrderRecorder.order.add("other spec")
                    assert 2 == 2
                }

            }

        }
    }

    @Test
    public void itRunsOnlyTheCleanBlocksForTheBehaviorContext () {
        SpecRunner runner = new SpecRunner(CleanContextsSpec)

        assertBehaviorPasses(runner, runner.getChildren()[0])
        assert OrderRecorder.order == [ 'spec', 'clean 1' ]

        OrderRecorder.order.clear()

        assertBehaviorPasses(runner, runner.getChildren()[1])
        assert OrderRecorder.order == [ 'other spec', 'clean 2' ]
    }

    static class CleanNestedContextsSpec implements Spec {

        int someNumber = 0

        @Describe("Clean Block Spec")
        def spec() {

            when 'something happens', {

                when 'things are the case', {

                    clean {
                        OrderRecorder.order.add("clean 1")
                    }

                    it 'runs a spec', {
                        OrderRecorder.order.add("spec")
                        assert someNumber == 0
                    }

                }

                clean {
                    OrderRecorder.order.add("clean 2")
                }

                clean {
                    OrderRecorder.order.add("clean 3")
                }

            }

            clean {
                OrderRecorder.order.add("clean 4")
            }

        }
    }

    @Test
    public void itRunsNestedCleanBlocksInOrderFromInsideOut () {
        SpecRunner runner = new SpecRunner(CleanNestedContextsSpec)

        assertBehaviorPasses(runner, runner.getChildren()[0])
        assert OrderRecorder.order == [ 'spec', 'clean 1', 'clean 2', 'clean 3', 'clean 4' ]
    }

}

