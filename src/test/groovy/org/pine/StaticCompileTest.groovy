package org.pine

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.junit.Test
import org.junit.runner.RunWith
import org.pine.annotation.Describe
import org.pine.annotation.SpecDelegate
import org.pine.testHelpers.FunSpecDelegate
import org.pine.testHelpers.TestHelper

class StaticCompileTest {

    @CompileStatic
    @RunWith(SpecRunner)
    class CompiledStaticSpec implements Spec {
        @Describe("A Statically Compiled Spec")
        def spec() {
            it "always works", {
                assert 1 == 1
            }

            when "we want to run a test", {
                it "understands basic arithmetic", {
                    assert 1 == 1
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
    void itHandlesItBlocksWithinWhenBlocks() {
        TestHelper.assertSpecRuns(CompiledStaticSpec, 0, 6, 0, true)
    }

    @CompileStatic
    @RunWith(SpecRunner)
    class CompiledStaticDescribeSpec implements Spec {
        @Describe("A Statically Compiled Spec with Describe")
        def spec() {
            describe "Simulated describe block", {
                it "executes a test", {
                    assert 1 == 1
                }
            }
        }
    }

    @Test
    void itHandlesItBlocksWithinDescribeBlocks() {
        TestHelper.assertSpecRuns(CompiledStaticDescribeSpec, 0, 1, 0, true)
    }

    @TypeChecked(extensions = "PineTypeCheckingExtension.groovy")
    @RunWith(SpecRunner)
    class CompiledStaticDelegateSpec implements Spec {

        @SpecDelegate
        public FunSpecDelegate funSpecDelegate = new FunSpecDelegate()

        @Describe("A Statically Compiled Spec with a SpecDelegate")
        def spec() {
            describe "Simulated describe block", {
                it "executes a test", {
                    assert getFun() == "Bowling!"
                }
            }
        }
    }

    @Test
    void itHandlesSpecDelegatesWithTheTypeCheckingExtension() {
        TestHelper.assertSpecRuns(CompiledStaticDelegateSpec, 0, 1, 0, true)
    }

    @TypeChecked(extensions = "PineTypeCheckingExtension.groovy")
    @RunWith(SpecRunner)
    class ParentDelegateSpec implements Spec {

        @SpecDelegate
        public FunSpecDelegate funSpecDelegate = new FunSpecDelegate()

    }

    @TypeChecked(extensions = "PineTypeCheckingExtension.groovy")
    class ChildDelegateSpec extends ParentDelegateSpec {
        @Describe("A Type Checked Spec with an inherited SpecDelegate")
        def spec() {
            it "executes a test", {
                assert getFun() == "Bowling!"
            }
        }
    }

    @Test
    void itHandlesInheritedSpecDelegatesWithTheTypeCheckingExtension() {
        TestHelper.assertSpecRuns(ChildDelegateSpec, 0, 1, 0, true)
    }

}