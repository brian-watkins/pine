package org.pine

import org.junit.Before
import org.junit.Test
import org.junit.runner.notification.RunNotifier
import org.pine.testHelpers.SpecTestRunListener
import org.pine.testHelpers.TestHelper

import static org.assertj.core.api.Assertions.*;

class FunSpecRunnerTest {

    FunSpecRunner runner

    @Before
    void setUp() {
        String script = '''
@groovy.transform.BaseScript org.pine.SpecScript spec

it 'understands basic arithmetic', {
    assert 1 == 0
}
'''
        Class scriptClass = TestHelper.getClassForScript(script);

        runner = new FunSpecRunner(scriptClass)
    }

    @Test
    void itReadsASpecFromAGroovyScript () {
        List<SpecScript> specs = runner.getChildren()

        assertThat(specs.size()).isEqualTo(1)
    }

    @Test
    void itGetsTheDescriptionOfTheSpec () {
        List<SpecScript> specs = runner.getChildren()

        assert runner.describeChild(specs.get(0)).getDisplayName() == 'it understands basic arithmetic'
    }

    @Test
    void itRunsTheFailingSpec () {
        SpecTestRunListener listener = new SpecTestRunListener()

        RunNotifier notifier = new RunNotifier()
        notifier.addFirstListener(listener)

        runner.runChild(runner.getChildren().get(0), notifier)

        assert listener.testsStarted == 1
        assert listener.failures == 1
        assert listener.testsFinished == 1
    }

}