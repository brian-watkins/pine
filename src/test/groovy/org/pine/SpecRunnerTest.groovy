package org.pine

import org.junit.Before
import org.junit.Test
import org.junit.runner.notification.RunNotifier
import org.pine.annotation.Describe
import org.pine.testHelpers.SpecTestRunListener
import org.pine.exception.SpecNotFoundException

import static groovy.test.GroovyAssert.shouldFail
import static org.assertj.core.api.Assertions.*;

class SpecRunnerTest {

    SpecRunner runner

    class SpecRunnerSpec implements Spec {
        @Describe('specRunner')
        def spec () {
            it 'understands basic arithmetic', {
                assert 1 == 0
            }
        }
    }

    @Before
    void setUp() {
        runner = new SpecRunner(SpecRunnerSpec)
    }

    @Test
    void itReadsASpecFromAGroovyScript () {
        List<Spec> specs = runner.getChildren()

        assertThat(specs.size()).isEqualTo(1)
    }

    @Test
    void itGetsTheDescriptionOfTheSpec () {
        List<Spec> specs = runner.getChildren()

        assert runner.describeChild(specs.get(0)).getDisplayName() == 'it understands basic arithmetic(org.pine.SpecRunnerTest$SpecRunnerSpec)'
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

    class SpecWithoutDescribe implements Spec {
        def spec() {
            it 'will not run', {
                assert 1 == 0
            }
        }
    }

    @Test
    void itThrowsExceptionWhenNoDescribeFound() {
        shouldFail SpecNotFoundException, {
            new SpecRunner(SpecWithoutDescribe)
        }
    }

}