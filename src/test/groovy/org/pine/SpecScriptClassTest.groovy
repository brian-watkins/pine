package org.pine

import org.junit.Before
import org.junit.Test
import org.pine.script.annotation.SpecScriptClass
import org.pine.testHelpers.FunAnnotation
import org.pine.testHelpers.TestHelper

import static groovy.test.GroovyAssert.shouldFail

class SpecScriptClassTest {

    GroovyClassLoader classLoader = new GroovyClassLoader()
    Class specScriptClass

    @Before
    public void setUp() {
        String script = '''
@groovy.transform.BaseScript org.pine.script.SpecScript spec

@org.pine.testHelpers.FunAnnotation("Books")
@org.pine.script.annotation.SpecScriptClass class config {}

it 'runs a spec', {
    assert 1 == 1
}
'''

        specScriptClass = classLoader.parseClass(script, "MyClass.groovy")
    }

    @Test
    public void itTransfersAnnotationsToTheScriptClass() {
        FunAnnotation annotation = specScriptClass.getAnnotation(FunAnnotation)

        assert annotation != null
        assert annotation.value() == 'Books'

        assert specScriptClass.getAnnotation(SpecScriptClass) == null
    }

    @Test
    public void itRemovesTheConfigClass () {
        shouldFail {
            classLoader.loadClass("config")
        }
    }

    @Test
    public void itDoesNotOtherwiseMessWithTheScript () {
        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }
}
