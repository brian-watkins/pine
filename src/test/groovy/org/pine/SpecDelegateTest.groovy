package org.pine

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement
import org.pine.annotation.Describe
import org.pine.testHelpers.TestHelper

class SpecDelegateTest {

    @Test
    public void itAssignsADelegateToTheBehaviorClosure() {
        String script = '''
import org.pine.annotation.*
import org.pine.testHelpers.*

@groovy.transform.BaseScript org.pine.script.SpecScript spec

@SpecDelegate @groovy.transform.Field
public FunSpecDelegate delegate = new FunSpecDelegate()

it 'runs a spec', {
    assert getFun() == "Bowling!"
}
'''

        Class specScriptClass = TestHelper.getClassForScript(script)

        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }

    @Test
    public void itAssignsADelegateToTheBehaviorClosureWithinOtherClosures() {
        String script = '''
import org.pine.annotation.*
import org.pine.testHelpers.*

@groovy.transform.BaseScript org.pine.script.SpecScript spec

@SpecDelegate @groovy.transform.Field
public FunSpecDelegate delegate = new FunSpecDelegate()

describe 'some spec', {
    when 'there is a delegate', {
        it 'runs a spec that is aware ofthe delegate', {
            assert getFun() == "Bowling!"
        }
    }
}
'''

        Class specScriptClass = TestHelper.getClassForScript(script)

        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }

    @Test
    public void itAssignsADelegateToTheAssumeClosure() {
        String script = '''
import org.pine.annotation.*
import org.pine.testHelpers.*

@groovy.transform.BaseScript org.pine.script.SpecScript spec

@SpecDelegate @groovy.transform.Field
public FunSpecDelegate delegate = new FunSpecDelegate()

def myFunThing = "swimming"

describe 'my spec', {
    assume {
        myFunThing = getFun()
    }

    it 'runs a spec', {
        assert myFunThing == "Bowling!"
    }
}
'''

        Class specScriptClass = TestHelper.getClassForScript(script)

        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }

    @Test
    public void itAssignsADelegateToTheCleanClosure() {
        String script = '''
import org.pine.annotation.*
import org.pine.testHelpers.*

@groovy.transform.BaseScript org.pine.script.SpecScript spec

@SpecDelegate @groovy.transform.Field
public FunSpecDelegate delegate = new FunSpecDelegate()

def myFunThing = "swimming"

clean {
    myFunThing = getFun()
}

it 'runs a spec', {
    assert 1 == 1
}
'''

        Class specScriptClass = TestHelper.getClassForScript(script)

        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }


    @Test
    public void itUsesADelegateThatIsInitializedByARule () {
        String script = '''
import org.pine.annotation.*
import org.pine.testHelpers.*
import groovy.transform.*

@BaseScript org.pine.script.SpecScript spec

@org.junit.Rule
@Field public TestInjectRule rule = new TestInjectRule()

@TestInject
@SpecDelegate @Field
public FunSpecDelegate delegate

describe 'some spec', {
    it 'runs a spec', {
        assert getFun() == "Bowling!"
    }
}
'''

        Class specScriptClass = TestHelper.getClassForScript(script)

        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }

}
