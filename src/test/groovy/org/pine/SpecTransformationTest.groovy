package org.pine

import org.pine.testHelpers.TestHelper
import org.junit.Before
import org.junit.Test

import static groovy.test.GroovyAssert.*

class SpecTransformationTest {

    Class scriptClass;

    @Before
    public void setUp() {
        String script = '''
@groovy.transform.BaseScript org.pine.SpecScript spec

@org.pine.Spec describing = "Annotations Test"

it 'does stuff', {
  assert 1 == 1
}
'''
        scriptClass = TestHelper.getClassForScript(script);
    }

    @Test
    public void itAddsReadOnlySpecNamePropertyToClass() {
        assert scriptClass.newInstance().specName == "Annotations Test"
        assert scriptClass.newInstance().getSpecName() == "Annotations Test"

        shouldFail {
            scriptClass.setSpecName("can't do this")
        }

        shouldFail {
            scriptClass.specName = 'will not work'
        }
    }
}