package org.pine

import org.pine.testHelpers.TestHelper
import org.junit.Test

class RuleTest {

    @Test
    public void itAddsAndAppliesAJUnitClassRuleOnAField () {
        String script = '''
import groovy.transform.*
import SpecScript
import FunRule
import org.junit.ClassRule

@BaseScript SpecScript spec

@ClassRule
@Field public static FunRule FUN_RULE = new FunRule()

it 'finds the rule variable', {
    assert this.FUN_RULE.funType == 'bowling'
}
'''

        TestHelper.assertSpecRuns(script, 0, 1, true)
    }

    @Test
    public void itAddsAndAppliesAJUnitClassRuleOnAMethod () {
        String script = '''
import groovy.transform.*
import bw.funSpec.*
import org.junit.*
import org.junit.runners.model.*
import org.junit.runner.*
import org.junit.rules.*

@BaseScript SpecScript spec

@Field public static String FUN_TYPE = 'running'

@ClassRule
public static TestRule getFunRule() {
    return new TestRule() {
        @Override
        Statement apply(Statement base, Description description) {
            println "Yo appling a class rule!"
            SuperSpec.FUN_TYPE = 'bowling'
            return base;
        }
    }
}

it 'finds the variable set by the rule', {
    assert SuperSpec.FUN_TYPE == 'bowling'
}
'''

        TestHelper.assertSpecRuns(script, "SuperSpec", 0, 1, true)
    }

    @Test
    public void itAddsAndAppliesAJUnitRuleOnAField () {
        String script = '''
import groovy.transform.*
import SpecScript
import FunRule
import org.junit.Rule

@BaseScript SpecScript spec

@Rule
@Field public FunRule funRule = new FunRule()

it 'finds the rule variable', {
    assert this.funRule.funType == 'bowling'
}
'''

        TestHelper.assertSpecRuns(script, 0, 1, true)
    }

    @Test
    public void itAddsAndAppliesAJUnitRuleOnAMethod () {
        String script = '''
import groovy.transform.*
import bw.funSpec.*
import org.junit.*
import org.junit.runners.model.*
import org.junit.runner.*
import org.junit.rules.*

@BaseScript SpecScript spec

@Field String funType = 'running'

@Rule
public TestRule getFunRule() {
    return new TestRule() {
        @Override
        Statement apply(Statement base, Description description) {
            this.funType = 'bowling'
            return base;
        }
    }
}

it 'finds the variable set by the rule', {
    assert this.funType == 'bowling'
}
'''

        TestHelper.assertSpecRuns(script, 0, 1, true)
    }

    @Test
    public void itAddsAndAppliesAJUnitMethodRuleOnAField () {
        String script = '''
import groovy.transform.*
import SpecScript
import FunMethodRule
import org.junit.Rule

@BaseScript SpecScript spec

@Rule
@Field public FunMethodRule funMethodRule = new FunMethodRule()

it 'finds the rule variable', {
    assert funMethodRule.methodName == 'run_behavior'
}
'''

        TestHelper.assertSpecRuns(script, 0, 1, true)
    }

    @Test
    public void itAddsAndAppliesAJUnitMethodRuleOnAMethod () {
        String script = '''
import groovy.transform.*
import bw.funSpec.*
import org.junit.*
import org.junit.runners.model.*
import org.junit.runner.*
import org.junit.rules.*

@BaseScript SpecScript spec

@Field String funType = 'running'

@Rule
public MethodRule getFunRule() {
    return new MethodRule() {
        @Override
        Statement apply(Statement base, FrameworkMethod method, Object target) {
            this.funType = method.getMethod().name
            return base;
        }
    }
}

it 'finds the variable set by the rule', {
    assert this.funType == 'run_behavior'
}
'''

        TestHelper.assertSpecRuns(script, 0, 1, true)
    }
}

