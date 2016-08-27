package org.pine.testHelpers

import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener

class SpecTestRunListener extends RunListener {
    int failures = 0
    int testsStarted = 0
    int testsFinished = 0

    List failureMessages = new ArrayList<String>()

    @Override
    public void testStarted (Description description) {
        testsStarted++
    }

    @Override
    public void testFailure (Failure failure) {
        failures++
        failureMessages.add(failure.getMessage())
    }

    @Override
    public void testFinished (Description description) {
        testsFinished++
    }
}
