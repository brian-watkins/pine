package org.pine.testHelpers

import org.junit.Rule

class FunSpecRuleDelegate {

    @Rule
    public FunRule funRule = new FunRule()

    def String getFun() {
        return this.funRule.funType
    }
}
