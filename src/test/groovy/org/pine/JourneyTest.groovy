package org.pine

import org.junit.Before
import org.junit.Test
import org.pine.annotation.Describe
import org.pine.behavior.Behavior
import org.pine.testHelpers.TestHelper

class JourneyTest {

    static int step = 0
    static List<String> cleanSteps = new ArrayList<>()

    @Before
    public void setUp() {
        step = 0
        cleanSteps.removeAll()
    }

    class NoNameJourneySpec implements JourneySpec {
        @Describe
        def mySpec() {

        }
    }

    @Test
    public void itSetsClassNameAsJourneyNameWhenNoneGiven() {
        SpecRunner runner = new SpecRunner(NoNameJourneySpec)
        Spec spec = runner.getSpec()

        assert spec.getBehaviors().get(0).getName() == 'org.pine.JourneyTest$NoNameJourneySpec'
    }

    String noNameJourneySpecScript = '''
@groovy.transform.BaseScript org.pine.script.JourneySpecScript spec

def step = 0

it 'does one thing', {
    step += 1
    assert 1 == step
    println "hey"
    assert "Bowling!" == getFun()
}

it 'does another thing', {
    step += 1
    assert 2 == step
    println "hey 2"
    assert "Bowling!" == getFun()
}

it 'does a final thing', {
    step += 1
    assert 3 == step
    println "hey 3"
    assert "Bowling!" == getFun()
}

'''

    @Test
    public void itSetsClassNameAsJourneyNameWhenNoneGivenInSpec() {
        Class specScriptClass = TestHelper.getClassForScript(noNameJourneySpecScript, "MyFunJourneySpec")
        SpecRunner runner = new SpecRunner(specScriptClass)
        Spec spec = runner.getSpec()

        assert spec.getBehaviors().get(0).getName() == 'MyFunJourneySpec'
    }

    static class SimpleJourneySpec implements JourneySpec {

        @Describe(value = "My Journey")
        def journey () {
            it 'does one thing', {
                step += 1
                println "Hey ${step}"
                assert 1 == step
            }

            it 'does another thing', {
                step += 1
                println "Hey ${step}"
                assert 2 == step
            }

            it 'does a final thing', {
                step += 1
                println "Hey ${step}"
                assert 3 == step
            }
        }
    }

    @Test
    public void describeAnnotationSetsTheJourneyName() {
        SpecRunner runner = new SpecRunner(SimpleJourneySpec)
        Spec spec = runner.getSpec()

        assert spec.getBehaviors().get(0).getName() == "My Journey"
    }

    @Test
    public void itRunsAllItBlocksAsOneTest() {
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
        println "hey"
        assert "Bowling!" == getFun()
    }

    it 'does another thing', {
        step += 1
        assert 2 == step
        println "hey 2"
        assert "Bowling!" == getFun()
    }

    it 'does a final thing', {
        step += 1
        assert 3 == step
        println "hey 3"
        assert "Bowling!" == getFun()
    }
}
'''

    @Test
    public void describeJourneySetsTheSpecDetails() {
        Class specScriptClass = TestHelper.getClassForScript(delegateJourneySpec)
        SpecRunner runner = new SpecRunner(specScriptClass)
        Spec spec = runner.getSpec()

        assert spec.getBehaviors().get(0).getName() == "My Journey With a Delegate"
    }

    @Test
    public void itMakesTheDelegateAvailableThroughoutTheJourney() {
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
        println "hey ${step}"
    }

    it 'does another thing', {
        step += 1
        assert 12 == step
        println "hey ${step}"
    }

    it 'does a final thing', {
        step += 1
        assert 13 == step
        println "hey ${step}"
    }
}
'''

    @Test
    public void itRunsAllAssumptions() {
        Class specScriptClass = TestHelper.getClassForScript(assumptionsJourneySpec)

        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }


    static class SimpleWhenJourneySpec implements JourneySpec {

        @Describe("My When Journey")
        def journey () {
            it 'does one thing', {
                step += 1
                println "Hey ${step}"
                assert 1 == step
            }

            when 'other things happen', {
                it 'does another thing', {
                    step += 1
                    println "Hey ${step}"
                    assert 2 == step
                }

                it 'does a final thing', {
                    step += 1
                    println "Hey ${step}"
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
    public void journeyNameIncludesWhenBlocks() {
        SpecRunner runner = new SpecRunner(SimpleWhenJourneySpec)
        Spec spec = runner.getSpec()

        List<Behavior> behaviors = spec.getBehaviors()
        assert behaviors.get(0).getName() == "My When Journey, when other things happen, and that other thing happens"
    }

    @Test
    public void itIncludesBehaviorsWithinWhenAsPartOfTheJourney() {
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
            println "hey ${step}"
        }

        it 'does another thing', {
            step += 1
            assert 12 == step
            println "hey ${step}"
        }

        it 'does a final thing', {
            step += 1
            assert 13 == step
            println "hey ${step}"
        }

    }
}
'''

    @Test
    public void itRunsAssumptionsWithinAWhenBlock() {
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
            println "hey ${step}"
        }

    }

    when "step is increased by 4", {

        it 'does another thing', {
            step += 4
            assert 5 == step
            println "hey ${step}"
        }

    }

    when "step is increased by 7", {

        it 'does a final thing', {
            step += 7
            assert 8 == step
            println "hey ${step}"
        }

    }
}
'''

    @Test
    public void itRunsOneTestForEachWhenBlock() {
        Class specScriptClass = TestHelper.getClassForScript(multipleWhenJourneySpec)

        TestHelper.assertSpecRuns(specScriptClass, 0, 3, true)
    }

    String multipleWhenAssumptionsJourneySpec = '''
@groovy.transform.BaseScript org.pine.script.JourneySpecScript spec

describe 'My Journey With Multiple Whens', {
    def step = 0

    assume {
        step = 10
    }

    it "does one thing", {
        step += 1
        assert 11 == step
    }

    when "step is increased by 1", {

        assume {
            step += 1
        }

        it 'does one thing', {
            assert 12 == step
            println "hey ${step}"
        }

    }

    when "step is increased by 4", {

        assume {
            step += 4
        }

        it 'does another thing', {
            assert 15 == step
            println "hey ${step}"
        }

    }

    when "step is increased by 7", {

        assume {
            step += 7
        }

        it 'does a final thing', {
            assert 18 == step
            println "hey ${step}"
        }

    }
}
'''

    @Test
    public void itRunsTheProperAssumptionsForEachWhenBlock() {
        Class specScriptClass = TestHelper.getClassForScript(multipleWhenAssumptionsJourneySpec)

        TestHelper.assertSpecRuns(specScriptClass, 0, 3, true)
    }

    String multipleCleanJourneySpec = '''
@groovy.transform.BaseScript org.pine.script.JourneySpecScript spec

describe 'My Journey With Multiple Cleans', {
    def step = 0

    assume {
        step = 10
    }

    clean {
        getCleanSteps().add('clean 1')
    }

    it "does one thing", {
        step += 1
        assert 11 == step
    }

    when "step is increased by 1", {

        assume {
            step += 1
        }

        clean {
            getCleanSteps().add('clean 2')
        }

        it 'does one thing', {
            assert 12 == step
            println "hey ${step}"
        }

    }

    when "step is increased by 4", {

        assume {
            step += 4
        }

        clean {
            getCleanSteps().add('clean 3')
        }

        it 'does another thing', {
            assert 15 == step
            println "hey ${step}"
        }

    }

    when "step is increased by 7", {

        assume {
            step += 7
        }

        clean {
            getCleanSteps().add('clean 4')
        }

        clean {
            getCleanSteps().add('clean 5')
        }

        it 'does a final thing', {
            assert 18 == step
            println "hey ${step}"
        }

    }
}
'''

    @Test
    public void itExecutesCleansFromOutsideIn() {
        Class specScriptClass = TestHelper.getClassForScript(multipleCleanJourneySpec)
        specScriptClass.metaClass.static.getCleanSteps << { _ ->  cleanSteps }

        TestHelper.assertSpecRuns(specScriptClass, 0, 3, true)

        assert cleanSteps ==
                [ 'clean 1', 'clean 2', 'clean 1', 'clean 3', 'clean 1', 'clean 4', 'clean 5']
    }


    String personaJourneySpec = '''
@groovy.transform.BaseScript org.pine.script.JourneySpecScript spec

describe "holly", "does some fun stuff", {

    assume {
        step = 0
    }

    holly "does one thing", {
        step += 1
        assert 1 == step
    }

    holly "does something else", {
        step += 5
        assert 6 == step
    }

    it "does another thing", {
        step += 2
        assert 8 == step
    }

}
'''

    @Test
    public void itNamesTheJourneyWhenThereIsAPersona() {
        Class specScriptClass = TestHelper.getClassForScript(personaJourneySpec)
        SpecRunner runner = new SpecRunner(specScriptClass)
        Spec spec = runner.getSpec()

        assert spec.getBehaviors().get(0).getName() == "holly does some fun stuff"
    }

    @Test
    public void itExecutesExamplesForThePersona() {
        Class specScriptClass = TestHelper.getClassForScript(personaJourneySpec)

        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }
}
