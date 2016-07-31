package org.pine

import org.junit.Test
import org.junit.runner.JUnitCore
import org.junit.runner.Result
import org.junit.runner.RunWith
import org.pine.annotation.Describe
import org.pine.testHelpers.TestHelper

class IntegrationTest {

    @RunWith(SpecRunner.class)
    class IntegrationSpec implements Spec {

        @Describe('failing test')
        def spec () {
            it 'understands basic arithmetic', {
                assert 1 == 0
            }
        }
    }

    @Test
    public void itRunsAFailingTest () {
        JUnitCore core = new JUnitCore();
        Result result = core.run(IntegrationSpec);

        assert result.failureCount == 1
        assert result.failures[0].description.displayName == 'it understands basic arithmetic(org.pine.IntegrationTest$IntegrationSpec)'
        assert result.failures[0].exception instanceof AssertionError
        assert result.runCount == 1
        assert result.wasSuccessful() == false
    }

    @RunWith(SpecRunner.class)
    class SuccessfulSpec implements Spec {
        String name = 'blah'

        @Describe('Successful test')
        public void run () {
            it 'understands basic arithmetic', {
                assert name == 'blah'
            }
        }
    }

    @Test
    public void itRunsASuccessfulTest() {
        TestHelper.assertSpecRuns(SuccessfulSpec, 0, 1, true);
    }

    @RunWith(SpecRunner.class)
    class AssumptionsSpec implements Spec {
        int someNumber = 0;

        @Describe('Assumptions Test')
        def spec () {
            assume {
                someNumber = 1
            }

            it 'understands basic arithmetic', {
                assert someNumber == 1
            }
        }
    }

    @Test
    public void itMakesAssumptionsUsedInTests() {
        TestHelper.assertSpecRuns(AssumptionsSpec, 0, 1, true);
    }

    @RunWith(SpecRunner)
    class MultipleTestsAndAssumptions implements Spec {

        def one = []

        @Describe('Multiple tests and assumptions')
        def spec() {
            assume {
                one << 'one'
            }

            it 'has one thing', {
                assert one.size() == 1
                assert one[0] == 'one'
            }

            it 'has nothing else', {
                assert one.size() == 1
                assert one[0] == 'one'
            }
        }

    }

    @Test
    public void itHasMultipleTestsAndAssumptions () {
        TestHelper.assertSpecRuns(MultipleTestsAndAssumptions, 0, 2, true);
    }

    @RunWith(SpecRunner)
    class WhenBlocks implements Spec {

        def one = []

        @Describe('When Blocks')
        def spec () {
            assume {
                one << 'one'
            }

            when 'another thing is added', {
                def secondThing = 'two'

                assume {
                    one << secondThing
                }

                it 'has two things', {
                    assert one.size() == 2
                    assert one[0] == 'one'
                    assert one[1] == secondThing
                }
            }

            it 'has one thing', {
                assert one.size() == 1
                assert one[0] == 'one'
            }

            it 'has nothing else', {
                assert one.size() == 1
                assert one[0] == 'one'
            }
        }
    }

    @Test
    public void itRespectsWhenBlocks () {
        TestHelper.assertSpecRuns(WhenBlocks, 0, 3, true);
    }


}


