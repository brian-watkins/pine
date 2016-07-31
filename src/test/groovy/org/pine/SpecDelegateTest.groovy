package org.pine

import org.junit.Test
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
}
