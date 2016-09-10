package org.pine

import org.junit.Test
import org.junit.runner.RunWith
import org.pine.annotation.Assume
import org.pine.annotation.Describe

import static org.pine.testHelpers.TestHelper.assertSpecRuns

class AssumptionsAnnotationTest {

    @RunWith(SpecRunner)
    class AnnotatedAssumptions implements Spec {

        int someNumber = 0

        @Assume
        def setup() {
            someNumber++
        }

        @Assume
        def setup_more() {
            someNumber++
        }

        @Assume
        def setup_even_more() {
            someNumber++
        }

        @Describe("Assume Annotation Spec")
        def spec() {
            it 'finds the number', {
                assert someNumber == 3
            }

            when 'there is another assumption', {
                assume {
                    someNumber++
                }

                it 'finds an even bigger number', {
                    assert someNumber == 4
                }
            }

            when 'there are assume blocks', {
                assume {
                    someNumber = 14
                }

                it 'runs the assume blocks after the assume methods', {
                    assert someNumber == 14
                }
            }
        }
    }

    @Test
    public void itRunsTheAnnotatedAssumptionsBeforeEachSpec() {
        assertSpecRuns(AnnotatedAssumptions, 0, 3, true)
    }

    class SuperAssumptions {
        int someNumber = 0

        @Assume
        public void setUp() {
            someNumber++
        }
    }

    @RunWith(SpecRunner)
    class AssumeSuperClassTest extends SuperAssumptions implements Spec {
        @Describe('Super class assumptions')
        def spec () {
            it 'handles the superclass assumption', {
                assert someNumber == 1
            }
        }
    }

    @Test
    public void itRunsTheSuperclassAssumptionMethods() {
        assertSpecRuns(AssumeSuperClassTest, 0, 1, true)
    }

}
