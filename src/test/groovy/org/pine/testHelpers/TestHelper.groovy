package org.pine.testHelpers

import org.junit.runner.JUnitCore
import org.junit.runner.Request
import org.junit.runner.Result
import org.pine.Spec

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
        JUnitCore core = new JUnitCore();
        Result result = core.run(spec);

        if (result.failureCount > 0) {
            println "Failure ****************************************"
            result.failures.each { failure -> println failure.message }
            println "************************************************"
        }

        assert result.runCount == runCount
        assert result.failureCount == failureCount
        if (ignoreCount > -1) {
            assert result.ignoreCount == ignoreCount
        }
        assert result.wasSuccessful() == wasSuccessful
    }

}
