package org.pine

import org.junit.Before
import org.junit.Test
import org.pine.annotation.Describe
import org.pine.behavior.Behavior
import org.pine.testHelpers.TestHelper
import org.pine.visitor.JourneySpecVisitor

class JourneyTest {

    static int step = 0
    static List<String> steps = new ArrayList<>()

    @Before
    public void setUp() {
        step = 0
        steps.clear()
    }

    protected static class SimpleJourneySpec implements JourneySpec {

        @Describe(value = "My Journey")
        def journey () {
            it 'does one thing', {
                step += 1
                assert 1 == step
            }

            it 'does another thing', {
                step += 1
                assert 2 == step
            }

            it 'does a final thing', {
                step += 1
                assert 3 == step
            }
        }
    }

    @Test
    void itRunsAllItBlocksAsOneTest() {
        SpecRunner runner = new SpecRunner(SimpleJourneySpec)

        TestHelper.assertBehaviorPasses(runner, runner.getChildren().get(0))
        assert step == 3
    }

    String delegateJourneySpec = '''
import org.pine.testHelpers.*
import org.pine.annotation.*

@groovy.transform.BaseScript org.pine.script.JourneySpecScript spec

@SpecDelegate @groovy.transform.Field
public FunSpecDelegate delegate = new FunSpecDelegate()

describe 'My Journey With a Delegate', {
    def step = 0

    it 'does one thing', {
        step += 1
        assert 1 == step
        assert "Bowling!" == getFun()
    }

    it 'does another thing', {
        step += 1
        assert 2 == step
        assert "Bowling!" == getFun()
    }

    it 'does a final thing', {
        step += 1
        assert 3 == step
        assert "Bowling!" == getFun()
    }
}
'''

    @Test
    void itMakesTheDelegateAvailableThroughoutTheJourney() {
        Class specScriptClass = TestHelper.getClassForScript(delegateJourneySpec)

        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }

    String assumptionsJourneySpec = '''
@groovy.transform.BaseScript org.pine.script.JourneySpecScript spec

describe 'My Journey With Assumptions', {
    def step = 0

    assume {
        step = 6
    }

    assume {
        step += 4
    }

    it 'does one thing', {
        step += 1
        assert 11 == step
    }

    it 'does another thing', {
        step += 1
        assert 12 == step
    }

    it 'does a final thing', {
        step += 1
        assert 13 == step
    }
}
'''

    @Test
    void itRunsAllAssumptions() {
        Class specScriptClass = TestHelper.getClassForScript(assumptionsJourneySpec)

        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }


    protected static class SimpleWhenJourneySpec implements JourneySpec {

        @Describe("My When Journey")
        def journey () {
            it 'does one thing', {
                step += 1
                assert 1 == step
            }

            when 'other things happen', {
                it 'does another thing', {
                    step += 1
                    assert 2 == step
                }

                it 'does a final thing', {
                    step += 1
                    assert 3 == step
                }

                when 'that other thing happens', {
                    it 'does something else', {
                        step += 1
                        assert 4 == step
                    }
                }
            }
        }
    }

    @Test
    void itIncludesBehaviorsWithinWhenAsPartOfTheJourney() {
        SpecRunner runner = new SpecRunner(SimpleWhenJourneySpec)

        TestHelper.assertBehaviorPasses(runner, runner.getChildren().get(0))
        assert step == 4
    }

    String whenAssumptionsJourneySpec = '''
@groovy.transform.BaseScript org.pine.script.JourneySpecScript spec

describe 'My Journey With When', {
    def step = 0

    assume {
        step = 6
    }

    when "step is increased by 4", {

        assume {
            step += 4
        }

        it 'does one thing', {
            step += 1
            assert 11 == step
        }

        it 'does another thing', {
            step += 1
            assert 12 == step
        }

        it 'does a final thing', {
            step += 1
            assert 13 == step
        }

    }
}
'''

    @Test
    void itRunsAssumptionsWithinAWhenBlock() {
        Class specScriptClass = TestHelper.getClassForScript(whenAssumptionsJourneySpec)

        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }

    String multipleWhenJourneySpec = '''
@groovy.transform.BaseScript org.pine.script.JourneySpecScript spec

describe 'My Journey With Multiple Whens', {
    def step = 0

    it "does one thing", {
        step += 1
        assert 1 == step
    }

    when "step is increased by 1", {

        it 'does one thing', {
            step += 1
            assert 2 == step
        }

    }

    when "step is increased by 4", {

        it 'does another thing', {
            step += 4
            assert 5 == step
        }

    }

    when "step is increased by 7", {

        it 'does a final thing', {
            step += 7
            assert 8 == step
        }

    }
}
'''

    @Test
    void itRunsOneTestForEachWhenBlock() {
        Class specScriptClass = TestHelper.getClassForScript(multipleWhenJourneySpec)

        TestHelper.assertSpecRuns(specScriptClass, 0, 3, true)
    }

    String multipleWhenAssumptionsJourneySpec = '''
@groovy.transform.BaseScript org.pine.script.JourneySpecScript spec

describe 'My Journey With Multiple Whens', {
    def step = 0
    def expectedStep = 0

    assume {
        step = 10
        getSteps().add("assume 1")
    }

    it "does one thing", {
        step += 1
        assert expectedStep == step
        getSteps().add("example 1")
    }

    when "step is increased by 1", {

        assume {
            step += 1
            expectedStep = 12
            getSteps().add("assume 2")
        }

        it 'does one thing', {
            assert 12 == step
            getSteps().add("example 2")
        }

    }

    when "step is increased by 4", {

        assume {
            step += 4
            expectedStep = 15
            getSteps().add("assume 3")
        }

        it 'does another thing', {
            assert 15 == step
            getSteps().add("example 3")
        }

    }

    when "step is increased by 7", {

        assume {
            step += 7
            expectedStep = 18
            getSteps().add("assume 4")
        }

        it 'does a final thing', {
            assert 18 == step
            getSteps().add("example 4")
        }

    }
}
'''

    @Test
    void itRunsTheAssumptionsForEachJourneyBeforeExamples() {
        Class specScriptClass = TestHelper.getClassForScript(multipleWhenAssumptionsJourneySpec)
        specScriptClass.metaClass.static.getSteps << { _ ->  steps }

        TestHelper.assertSpecRuns(specScriptClass, 0, 3, true)

        assert steps ==
                [ 'assume 1', 'assume 2', 'example 1', 'example 2',
                  'assume 1', 'assume 3', 'example 1', 'example 3',
                  'assume 1', 'assume 4', 'example 1', 'example 4' ]
    }

    String multipleCleanJourneySpec = '''
@groovy.transform.BaseScript org.pine.script.JourneySpecScript spec

describe 'My Journey With Multiple Cleans', {

    clean {
        getCleanSteps().add('clean 1')
    }

    it "does one thing", {
        assert 1 == 1
    }

    when "things happen", {

        clean {
            getCleanSteps().add('clean 2')
        }

        it 'does one thing', {
            assert 2 == 2
        }

    }

    when "something else happens", {

        clean {
            getCleanSteps().add('clean 3')
        }

        it 'does another thing', {
            assert 3 == 3
        }

    }

    when "a final thing happens", {

        clean {
            getCleanSteps().add('clean 4')
        }

        clean {
            getCleanSteps().add('clean 5')
        }

        it 'does a final thing', {
            assert 4 == 4
        }

    }
}
'''

    @Test
    void itExecutesCleansFromOutsideIn() {
        Class specScriptClass = TestHelper.getClassForScript(multipleCleanJourneySpec)
        specScriptClass.metaClass.static.getCleanSteps << { _ ->  steps }

        TestHelper.assertSpecRuns(specScriptClass, 0, 3, true)

        assert steps ==
                [ 'clean 1', 'clean 2', 'clean 1', 'clean 3', 'clean 1', 'clean 4', 'clean 5']
    }


    protected static class SimplePronounJourneySpec implements JourneySpec {

        @Describe(value = "My Journey With Pronouns")
        def journey () {
            she 'does one thing', {
                step += 1
                assert 1 == step
            }

            he 'does another thing', {
                step += 1
                assert 2 == step
            }

            it 'does a final thing', {
                step += 1
                assert 3 == step
            }
        }
    }

    @Test
    void itExecutesExamplesForSheAndHe() {
        SpecRunner runner = new SpecRunner(SimplePronounJourneySpec)

        TestHelper.assertBehaviorPasses(runner, runner.getChildren().get(0))
        assert step == 3
    }
}
