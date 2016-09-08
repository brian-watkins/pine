package org.pine

import org.junit.ClassRule
import org.junit.Rule
import org.junit.rules.MethodRule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.pine.annotation.Describe
import org.pine.testHelpers.FunMethodRule
import org.pine.testHelpers.FunRule
import org.pine.testHelpers.TestHelper
import org.junit.Test

class RuleTest {

    @RunWith(SpecRunner)
    class RuleTest_1 implements Spec {
        @ClassRule
        public static FunRule FUN_RULE = new FunRule()

        @Describe('Spec')
        def spec() {
            it 'finds the rule variable', {
                assert this.FUN_RULE.funType == 'bowling'
            }
        }
    }

    @Test
    public void itAddsAndAppliesAJUnitClassRuleOnAField () {
        TestHelper.assertSpecRuns(RuleTest_1, 0, 1, true)
    }

    class SuperRuleTest {
        @ClassRule
        public static FunRule FUN_RULE = new FunRule()
    }

    @RunWith(SpecRunner)
    class InheritRuleTest_1 extends SuperRuleTest implements Spec {
        @Describe('Spec')
        def spec() {
            it 'finds the rule variable', {
                assert this.FUN_RULE.funType == 'bowling'
            }
        }
    }

    @Test
    public void itAddsAndAppliesAJUnitClassRuleOnASuperClassField () {
        TestHelper.assertSpecRuns(InheritRuleTest_1, 0, 1, true)
    }

    @RunWith(SpecRunner)
    class RuleTest_2 implements Spec {
        public static String FUN_TYPE = 'running'

        @ClassRule
        public static TestRule getFunRule() {
            return new TestRule() {
                @Override
                Statement apply(Statement base, Description description) {
                    println "Yo appling a class rule!"
                    RuleTest_2.FUN_TYPE = 'bowling'
                    return base;
                }
            }
        }


        @Describe('Spec')
        def spec() {
            it 'finds the variable set by the rule', {
                assert RuleTest_2.FUN_TYPE == 'bowling'
            }
        }
    }


    @Test
    public void itAddsAndAppliesAJUnitClassRuleOnAMethod () {
        TestHelper.assertSpecRuns(RuleTest_2, 0, 1, true)
    }

    @RunWith(SpecRunner)
    class RuleTest_3 implements Spec {
        @Rule
        public FunRule funRule = new FunRule()

        @Describe('spec')
        def spec() {
            it 'finds the rule variable', {
                assert this.funRule.funType == 'bowling'
            }
        }
    }

    @Test
    public void itAddsAndAppliesAJUnitRuleOnAField () {
        TestHelper.assertSpecRuns(RuleTest_3, 0, 1, true)
    }

    @RunWith(SpecRunner)
    class RuleTest_4 implements Spec {
        String funType = 'running'

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

        @Describe('spec')
        def spec() {
            it 'finds the variable set by the rule', {
                assert this.funType == 'bowling'
            }
        }
    }

    @Test
    public void itAddsAndAppliesAJUnitRuleOnAMethod () {
        TestHelper.assertSpecRuns(RuleTest_4, 0, 1, true)
    }

    class SuperRuleTest_4 {
        String funType = 'running'

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
    }

    @RunWith(SpecRunner)
    class InheritRuleTest_4 extends SuperRuleTest_4 implements Spec {
        @Describe('spec')
        def spec() {
            it 'finds the variable set by the rule', {
                assert this.funType == 'bowling'
            }
        }
    }

    @Test
    public void itAddsAndAppliesAJunitRuleOnASuperClassMethod() {
        TestHelper.assertSpecRuns(InheritRuleTest_4, 0, 1, true)
    }

    @RunWith(SpecRunner)
    class RuleTest_5 implements Spec {
        @Rule
        public FunMethodRule funMethodRule = new FunMethodRule()

        @Describe('spec')
        def gatherSpecs() {
            it 'finds the rule variable', {
                assert funMethodRule.methodName == 'gatherSpecs'
            }
        }
    }

    @Test
    public void itAddsAndAppliesAJUnitMethodRuleOnAField () {
        TestHelper.assertSpecRuns(RuleTest_5, 0, 1, true)
    }

    @RunWith(SpecRunner)
    class RuleTest_6 implements Spec {
        String funType = 'running'

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

        @Describe('spec')
        def mySpec () {
            it 'finds the variable set by the rule', {
                assert this.funType == 'mySpec'
            }
        }
    }

    @Test
    public void itAddsAndAppliesAJUnitMethodRuleOnAMethod () {
        TestHelper.assertSpecRuns(RuleTest_6, 0, 1, true)
    }
}

