package org.pine

import org.junit.Test
import org.pine.testHelpers.TestHelper

import java.util.stream.Collectors

/**
 * Created by bwatkins on 7/16/16.
 */
class WhenTest {

    @Test
    public void itNamesSpecsAccordingToTheirWhenBlock() {
        String script = '''
@groovy.transform.BaseScript SpecScript spec

it 'always works', {
    assert 1 == 1
}

when 'we want to run a test', {
    it 'understands basic arithmetic', {
        assert 1 == 0
    }

    it 'does fun stuff', {
        assert 1 == 1
    }
}

when 'we want to do something else', {
    it 'still understands arithmetic', {
        assert 1 == 1
    }

    when 'something unexpected happens', {
        it 'responds appropriately', {
            assert 1 == 1
        }
    }
}

it 'still works', {
    assert 1 == 1
}
'''
        Class scriptClass = TestHelper.getClassForScript(script);

        FunSpecRunner runner = new FunSpecRunner(scriptClass)

        List<Behavior> behaviors = runner.getChildren()

        assert behaviors.size() == 6

        def behaviorNames = behaviors.stream().map({ b -> b.description().displayName }).collect(Collectors.toList())

        assert behaviorNames.contains("it always works")
        assert behaviorNames.contains('when we want to run a test, it understands basic arithmetic')
        assert behaviorNames.contains('when we want to run a test, it does fun stuff')
        assert behaviorNames.contains('when we want to do something else, it still understands arithmetic')
        assert behaviorNames.contains('when we want to do something else, and something unexpected happens, it responds appropriately')
        assert behaviorNames.contains('it still works')
    }
}
