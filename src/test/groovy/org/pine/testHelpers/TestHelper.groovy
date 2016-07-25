package org.pine.testHelpers

import org.junit.runner.JUnitCore
import org.junit.runner.Result

/**
 * Created by bwatkins on 7/10/16.
 */
class TestHelper {

    public static Class getClassForScript(String script) {
        return getClassForScript(script, "MySpec")
    }

    public static Class getClassForScript(String script, String className) {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader()
        return groovyClassLoader.parseClass(script, className + ".groovy")
    }

    public static void assertSpecRuns(String script, int failureCount, int runCount, boolean wasSuccessful) {
        assertSpecRuns(script, "MySpec", failureCount, runCount, wasSuccessful)
    }

    public static void assertSpecRuns(String script, String className, int failureCount, int runCount, boolean wasSuccessful) {
        Class scriptClass = TestHelper.getClassForScript(script, className);

        JUnitCore core = new JUnitCore();
        Result result = core.run(scriptClass);

        if (result.failureCount > 0) {
            println "Failure ****************************************"
            result.failures.each { failure -> println failure.message }
            println "************************************************"
        }

        assert result.failureCount == failureCount
        assert result.runCount == runCount
        assert result.wasSuccessful() == wasSuccessful
    }

}
