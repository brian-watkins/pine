package org.pine.testHelpers

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class FunRule implements TestRule {

    def String funType;

    @Override
    Statement apply(Statement base, Description description) {
        funType = 'bowling'
        return base;
    }
}
