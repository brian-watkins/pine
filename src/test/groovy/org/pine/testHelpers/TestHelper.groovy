package org.pine.testHelpers

import org.junit.runner.JUnitCore
import org.junit.runner.Result
import org.junit.runner.notification.RunNotifier
import org.pine.behavior.Behavior
import org.pine.SpecRunner

class TestHelper {

    public static Class getClassForScript(String script) {
        return getClassForScript(script, "MySpec")
    }

    public static Class getClassForScript(String script, String className) {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader()
        return groovyClassLoader.parseClass(script, className + ".groovy")
    }

    public static void assertSpecRuns(Class spec, int failureCount, int runCount, boolean wasSuccessful) {
        assertSpecRuns(spec, failureCount, runCount, -1, wasSuccessful)
    }

    public static void assertSpecRuns (Class spec, int failureCount, int runCount, int ignoreCount, boolean wasSuccessful) {
        JUnitCore core = new JUnitCore()
        Result result = core.run(spec)

        if (result.failureCount > 0) {
            println "Failure ****************************************"
            result.failures.each { failure -> println failure.exception.printStackTrace() }
            println "************************************************"
        }

        assert result.runCount == runCount
        assert result.failureCount == failureCount
        if (ignoreCount > -1) {
            assert result.ignoreCount == ignoreCount
        }
        assert result.wasSuccessful() == wasSuccessful
    }

    public static void assertBehaviorPasses(SpecRunner runner, Behavior behavior) {
        SpecTestRunListener listener = new SpecTestRunListener()

        RunNotifier runNotifier = new RunNotifier()
        runNotifier.addFirstListener(listener)
        runner.runChild(behavior, runNotifier)

        if (listener.failures > 0) {
            println "Failure ***********************"
            listener.failureMessages.forEach { message -> println message }
            println "***********************"
        }

        assert listener.failures == 0
        assert listener.testsFinished > 0
    }

    public static void assertBehaviorFails(SpecRunner runner, Behavior behavior) {
        SpecTestRunListener listener = new SpecTestRunListener()

        RunNotifier runNotifier = new RunNotifier()
        runNotifier.addFirstListener(listener)
        runner.runChild(behavior, runNotifier)

        assert listener.failures == 1
        assert listener.testsFinished > 0
    }
}
