package org.pine

import org.junit.Test
import org.pine.annotation.Describe
import org.pine.behavior.Behavior
import org.pine.testHelpers.TestHelper
import org.pine.util.SpecClass
import org.pine.visitor.JourneySpecVisitor

class JourneySpecVisitorTest {

    protected class NoNameJourneySpec implements JourneySpec {
        @Describe
        def mySpec() {

        }
    }

    @Test
    void itSetsClassNameAsJourneyNameWhenNoneGiven() {
        JourneySpecVisitor visitor = visitSpec(NoNameJourneySpec)

        assert visitor.getBehaviors().get(0).getName() == 'org.pine.JourneySpecVisitorTest$NoNameJourneySpec'
    }

    String noNameJourneySpecScript = '''
@groovy.transform.BaseScript org.pine.script.JourneySpecScript spec

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

'''

    @Test
    void itSetsClassNameAsJourneyNameWhenNoneGivenInSpec() {
        Class specScriptClass = TestHelper.getClassForScript(noNameJourneySpecScript, "MyFunJourneySpec")

        JourneySpecVisitor visitor = visitSpec(specScriptClass)

        assert visitor.getBehaviors().get(0).getName() == 'MyFunJourneySpec'
    }

    protected static class DescribedJourneySpec implements JourneySpec {

        @Describe(value = "My Journey")
        def journey () {
            it 'does one thing', {
                assert 1 == 1
            }
        }
    }

    @Test
    void describeAnnotationSetsTheJourneyName() {
        JourneySpecVisitor visitor = visitSpec(DescribedJourneySpec)

        assert visitor.getBehaviors().get(0).getName() == "My Journey"
    }

    @Test
    void describeJourneySetsTheSpecDetails() {
        String delegateJourneySpec = '''
@groovy.transform.BaseScript org.pine.script.JourneySpecScript spec

describe 'My Journey Script', {

    it 'does one thing', {
        assert 1 == 1
    }
    
}
'''

        Class specScriptClass = TestHelper.getClassForScript(delegateJourneySpec)
        JourneySpecVisitor visitor = visitSpec(specScriptClass)

        assert visitor.getBehaviors().get(0).getName() == "My Journey Script"
    }

    protected static class MultipleWhenJourneySpec implements JourneySpec {

        @Describe("My When Journey")
        def journey () {
            it 'does one thing', {
                assert 1 == 1
            }

            when 'other things happen', {
                it 'does another thing', {
                    assert 2 == 2
                }

                when 'that other thing happens', {
                    it 'does something else', {
                        assert 4 == 4
                    }
                }

                when 'that other thing does not happen', {
                    it 'does the other thing', {
                        assert 3 == 3
                    }
                }
            }
        }
    }

    @Test
    void journeyNameIncludesWhenBlocks() {
        JourneySpecVisitor visitor = visitSpec(MultipleWhenJourneySpec)

        List<Behavior> behaviors = visitor.getBehaviors()
        assert behaviors.size() == 2
        assert behaviors.get(0).getName() == "My When Journey, when other things happen, and that other thing happens"
        assert behaviors.get(1).getName() == "My When Journey, when other things happen, and that other thing does not happen"
    }


    private JourneySpecVisitor visitSpec (Class specClass) {
        Spec spec = specClass.newInstance()
        JourneySpecVisitor visitor = new JourneySpecVisitor()
        visitor.prepare(new SpecClass(specClass), spec)
        spec.setSpecVisitor(visitor)
        visitor.getSpecMethod().invokeExplosively(spec)

        return visitor
    }
}
