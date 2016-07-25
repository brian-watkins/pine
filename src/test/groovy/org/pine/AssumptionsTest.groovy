package org.pine

import org.junit.Before
import org.junit.Test
import org.junit.runner.notification.RunNotifier
import org.pine.testHelpers.SpecTestRunListener
import org.pine.testHelpers.TestHelper

class AssumptionsTest {

    FunSpecRunner runner

    @Before
    void setUp() {
        def script = '''
@groovy.transform.BaseScript SpecScript spec

int someNumber = 0
int anotherNumber = 1

assume {
    someNumber = 1
}

assume {
    anotherNumber = anotherNumber + 1
}

when 'someNumber is updated', {
    assume { someNumber = 4 }

    it 'finds the updated number', {
        assert someNumber == 4
    }
}

it 'asserts about an assumption', {
    assert someNumber == 1

    anotherNumber = 7
    assert anotherNumber == 7
}

it 'asserts about another number', {
    assert someNumber == 1
    assert anotherNumber == 2
}
'''
        def SpecClass = TestHelper.getClassForScript(script)
        runner = new FunSpecRunner(SpecClass)
    }

    @Test
    public void itMakesAssumptionsUsedInTests () {
        assertBehaviorPasses(runner.getChildren().get(0))
    }

    @Test
    public void itResetsAssumptionsForEachTest () {
        List<Behavior> behaviors = runner.getChildren()

        assert behaviors.size == 3
        behaviors.each{ b -> assertBehaviorPasses(b) }
    }

    private assertBehaviorPasses(Behavior behavior) {
        SpecTestRunListener listener = new SpecTestRunListener()

        RunNotifier runNotifier = new RunNotifier()
        runNotifier.addFirstListener(listener)
        runner.runChild(behavior, runNotifier)

        assert listener.failures == 0
        assert listener.testsFinished > 0
    }

}
