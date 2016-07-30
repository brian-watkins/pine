package org.pine

import org.junit.Test
import org.pine.testHelpers.TestHelper

class ScriptTest {

    @Test
    public void itRunsASpecInAScriptAndDelegatesToAnotherClass() {
        String script = '''
@groovy.transform.BaseScript org.pine.SpecScript spec

it 'runs a spec', {
    assert 'fun' == 'fun'
}
'''

        Class specScriptClass = TestHelper.getClassForScript(script)

        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }
}
