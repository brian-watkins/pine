package org.pine

import org.junit.Test
import org.pine.testHelpers.TestHelper

class DescribeSpec {

    class DescribedSpec implements Spec {
        @Describe("FunnyController")
        def spec () {

        }
    }

    @Test
    public void itSetsSpecNameFromDescribeAnnotation() {
        SpecRunner runner = new SpecRunner(DescribedSpec)
        Spec describedSpec = runner.getSpec()

        assert describedSpec.specName == "FunnyController"
        assert describedSpec.getSpecName() == "FunnyController"
    }

    class NoNameSpec implements Spec {
        @Describe
        def mySpec() {

        }
    }

    @Test
    public void itSetsClassNameAsSpecNameFromDescribeAnnotationWhenNoneGiven() {
        SpecRunner runner = new SpecRunner(NoNameSpec)
        Spec describedSpec = runner.getSpec()

        assert describedSpec.specName == 'org.pine.DescribeSpec$NoNameSpec'
        assert describedSpec.getSpecName() == 'org.pine.DescribeSpec$NoNameSpec'
    }

    @Test
    public void itDescribesTheSpecFromDescribeMethodInScript() {
        String script = '''
@groovy.transform.BaseScript org.pine.SpecScript spec

describe 'MySpec', {
    it 'runs a spec', {
        assert 1 == 1
    }
}
'''

        Class specScriptClass = TestHelper.getClassForScript(script)
        SpecRunner runner = new SpecRunner(specScriptClass)
        Spec spec = runner.getSpec();

        assert spec.specName == 'MySpec'

        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }

    @Test
    public void itSetsClassNameAsSpecNameWhenNoneGivenInScript() {
        String script = '''
@groovy.transform.BaseScript org.pine.SpecScript spec

it 'runs a spec', {
    assert 1 == 1
}
'''

        Class specScriptClass = TestHelper.getClassForScript(script, "MyFunSpec")
        SpecRunner runner = new SpecRunner(specScriptClass)
        Spec spec = runner.getSpec();

        assert spec.specName == 'MyFunSpec'

        TestHelper.assertSpecRuns(specScriptClass, 0, 1, true)
    }

}