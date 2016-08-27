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

class FinalizeTest {

    static class OrderRecorder {
        static def order = new ArrayList<String>()
    }

    @RunWith(SpecRunner)
    static class FinalizeSpec implements Spec {

        int someNumber = 0

        @Describe("Finally Block Spec")
        def spec() {

            finalize {
                OrderRecorder.order.add("finalizer 1")
                someNumber = 1
            }

            finalize {
                OrderRecorder.order.add("finalizer 2")
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
    public void itRunsTheFinalizeBlocksInOrderAfterABehavior () {
        SpecRunner runner = new SpecRunner(FinalizeSpec)
        assertBehaviorPasses(runner, runner.getChildren()[0])

        assert OrderRecorder.order == [ 'spec', 'finalizer 1', 'finalizer 2' ]
    }

    @Test
    public void itRunsTheFinalizeBlocksAfterEachBehavior () {
        SpecRunner runner = new SpecRunner(FinalizeSpec)
        assertSpecRuns(FinalizeSpec, 0, 2, true)

        assert OrderRecorder.order == [ 'spec', 'finalizer 1', 'finalizer 2', 'other spec', 'finalizer 1', 'finalizer 2' ]
    }

    @RunWith(SpecRunner)
    static class FinalizeContextsSpec implements Spec {

        int someNumber = 0

        @Describe("Finally Block Spec")
        def spec() {

            when 'things are the case', {

                finalize {
                    OrderRecorder.order.add("finalizer 1")
                }

                it 'runs a spec', {
                    OrderRecorder.order.add("spec")
                    assert someNumber == 0
                }

            }

            when 'things are not the case', {

                finalize {
                    OrderRecorder.order.add("finalizer 2")
                }

                it 'does other things', {
                    OrderRecorder.order.add("other spec")
                    assert 2 == 2
                }

            }

        }
    }

    @Test
    public void itRunsOnlyTheFinalizerBlocksForTheBehaviorContext () {
        SpecRunner runner = new SpecRunner(FinalizeContextsSpec)

        assertBehaviorPasses(runner, runner.getChildren()[0])
        assert OrderRecorder.order == [ 'spec', 'finalizer 1' ]

        OrderRecorder.order.clear()

        assertBehaviorPasses(runner, runner.getChildren()[1])
        assert OrderRecorder.order == [ 'other spec', 'finalizer 2' ]
    }

    @RunWith(SpecRunner)
    static class FinalizeNestedContextsSpec implements Spec {

        int someNumber = 0

        @Describe("Finally Block Spec")
        def spec() {

            when 'something happens', {

                when 'things are the case', {

                    finalize {
                        OrderRecorder.order.add("finalizer 1")
                    }

                    it 'runs a spec', {
                        OrderRecorder.order.add("spec")
                        assert someNumber == 0
                    }

                }

                finalize {
                    OrderRecorder.order.add("finalizer 2")
                }

                finalize {
                    OrderRecorder.order.add("finalizer 3")
                }

            }

            finalize {
                OrderRecorder.order.add("finalizer 4")
            }

        }
    }

    @Test
    public void itRunsNestedFinalizerBlocksInOrderFromInsideOut () {
        SpecRunner runner = new SpecRunner(FinalizeNestedContextsSpec)

        assertBehaviorPasses(runner, runner.getChildren()[0])
        assert OrderRecorder.order == [ 'spec', 'finalizer 1', 'finalizer 2', 'finalizer 3', 'finalizer 4' ]
    }

}
