package org.pine

import org.junit.Test
import org.junit.runner.JUnitCore
import org.junit.runner.Result
import org.pine.testHelpers.TestHelper

/**
 * Created by bwatkins on 7/10/16.
 */
class IntegrationTest {

    @Test
    public void itRunsAFailingTest () {
        String script = '''
@groovy.transform.BaseScript SpecScript spec

it 'understands basic arithmetic', {
    assert 1 == 0
}
'''
        Class scriptClass = TestHelper.getClassForScript(script);

        JUnitCore core = new JUnitCore();
        Result result = core.run(scriptClass);

        assert result.failureCount == 1
        assert result.failures[0].description.displayName == 'it understands basic arithmetic'
        assert result.failures[0].exception instanceof AssertionError
        assert result.runCount == 1
        assert result.wasSuccessful() == false
    }

    @Test
    public void itRunsASuccessfulTest() {
        String script = '''
@groovy.transform.BaseScript SpecScript spec

name = 'blah'

it 'understands basic arithmetic', {
    assert name == 'blah'
}
'''
        TestHelper.assertSpecRuns(script, 0, 1, true);
    }

    @Test
    public void itMakesAssumptionsUsedInTests() {
        String script = '''
@groovy.transform.BaseScript SpecScript spec

int someNumber = 0

assume {
  someNumber = 1
}

it 'understands basic arithmetic', {
    assert someNumber == 1
}
'''
        TestHelper.assertSpecRuns(script, 0, 1, true);
    }

    @Test
    public void itHasMultipleTestsAndAssumptions () {
        String script = '''
@groovy.transform.BaseScript SpecScript spec

one = []

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

'''
        TestHelper.assertSpecRuns(script, 0, 2, true);
    }

    @Test
    public void itRespectsWhenBlocks () {
        String script = '''
@groovy.transform.BaseScript SpecScript spec

one = []

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

'''

        TestHelper.assertSpecRuns(script, 0, 3, true);
    }


}


