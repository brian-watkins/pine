package org.pine.statement

import org.junit.Rule
import org.junit.rules.MethodRule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.pine.Spec
import org.pine.util.SpecClass
import org.pine.annotation.SpecDelegate
import org.pine.util.ReflectionUtils

class RulesStatement extends Statement {

    private Statement statement
    private List<TestRule> testRules
    private List<MethodRule> methodRules
    private FrameworkMethod method
    private Description behaviorDescription
    private Spec specInstance

    RulesStatement (SpecClass specClass, Spec specInstance, FrameworkMethod specMethod, Description behaviorDescription, Statement statement) {
        this.statement = statement
        this.testRules = getTestRules(specClass, specInstance)
        this.methodRules = getMethodRules(specClass, specInstance)
        this.method = specMethod
        this.behaviorDescription = behaviorDescription
        this.specInstance = specInstance
    }

    @Override
    void evaluate() throws Throwable {
        Statement updatedStatement = this.statement

        this.methodRules.each { rule -> updatedStatement = rule.apply(updatedStatement, method, this.specInstance) }
        this.testRules.each { rule -> updatedStatement = rule.apply(updatedStatement, behaviorDescription) }

        updatedStatement.evaluate()
    }

    private List<TestRule> getTestRules(SpecClass specClass, Spec spec) {
        List<TestRule> rules = specClass.getAnnotatedFieldValues(spec, Rule.class, TestRule.class)
        rules.addAll(specClass.getAnnotatedMethodValues(spec, Rule.class, TestRule.class))
        rules.addAll(getTestRulesForDelegate(specClass, spec))

        return rules
    }

    private List<TestRule> getTestRulesForDelegate(SpecClass specClass, Spec spec) {
        List<TestRule> delegateRules;

        Optional<Object> scriptDelegate = specClass.getAnnotatedFieldValues(spec, SpecDelegate, Object).stream().findFirst()
        if (scriptDelegate.present) {
            delegateRules = ReflectionUtils.getAnnotatedFieldValues(scriptDelegate.get().class, scriptDelegate.get(), Rule.class, TestRule.class)
        } else {
            delegateRules = new ArrayList<>()
        }

        return delegateRules
    }

    private List<MethodRule> getMethodRules(SpecClass specClass, Spec spec) {
        List<MethodRule> rules = specClass.getAnnotatedFieldValues(spec, Rule.class, MethodRule.class)
        rules.addAll(specClass.getAnnotatedMethodValues(spec, Rule.class, MethodRule.class))

        return rules
    }
}