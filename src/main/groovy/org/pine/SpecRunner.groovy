package org.pine

import org.junit.Rule
import org.junit.rules.MethodRule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import org.junit.runners.ParentRunner
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.InitializationError
import org.junit.runners.model.Statement
import org.junit.runners.model.TestClass

import java.lang.reflect.Method

class SpecRunner extends ParentRunner<Behavior> {

    Class specClass
    def behaviors = []

    public SpecRunner(Class<?> testClass) throws InitializationError {
        super(testClass)

        this.specClass = testClass

        this.behaviors = getSpec().getBehaviors()
    }

    protected TestClass createTestClass(Class testClass) {
        println "Creating spec class for runner from class: ${testClass.name}"
        return new SpecClass(testClass)
    }

    public Spec getSpec() {
        Spec spec = specClass.newInstance()

        spec.invokeMethod(getSpecMethod(spec).name, null)

        if (spec.specName == null) {
            setSpecName(spec)
        }

        return spec
    }

    private Method getSpecMethod(Spec spec) {
        if (spec instanceof Script) {
            return spec.class.getMethod("run", null)
        }

        return Arrays.asList(specClass.getMethods()).stream()
                .filter({ method -> method.isAnnotationPresent(Describe.class) })
                .findFirst()
                .orElseThrow(SpecNotFoundException.metaClass.&invokeConstructor)
    }

    private void setSpecName (Spec spec) {
        Method specMethod = Arrays.asList(specClass.getMethods()).stream()
                .filter({ method -> method.isAnnotationPresent(Describe.class) })
                .findFirst().orElse(null)

        spec.setSpecName(specMethod?.getAnnotation(Describe.class)?.value() ?: spec.class.name)
    }

    @Override
    protected List<Behavior> getChildren() {
        return behaviors
    }

    @Override
    protected Description describeChild(Behavior child) {
        return child.description()
    }

    @Override
    protected void runChild(Behavior child, RunNotifier notifier) {
        Spec spec = getSpec()

        Behavior behavior = spec.behaviors.find{ b -> b.name == child.name }
        Description description = behavior.description()

        Statement childStatement = new BehaviorStatement(behavior)
        childStatement = new AssumptionsStatement(behavior.assumptions, childStatement)
        childStatement = new RulesStatement(spec, description, childStatement)

        runLeaf(childStatement, description, notifier)
    }

    class BehaviorStatement extends Statement {

        private Behavior behavior

        public BehaviorStatement (Behavior behavior) {
            this.behavior = behavior
        }

        @Override
        void evaluate() throws Throwable {
            behavior.block()
        }
    }

    class RulesStatement extends Statement {

        private Statement statement
        private List<TestRule> testRules
        private List<MethodRule> methodRules
        private FrameworkMethod method
        private Spec specInstance
        private Description behaviorDescription

        public RulesStatement (Spec specInstance, Description behaviorDescription, Statement statement) {
            this.statement = statement
            this.testRules = getTestRules(specInstance)
            this.methodRules = getMethodRules(specInstance)
            this.method = getFrameworkMethod(specInstance)
            this.specInstance = specInstance
            this.behaviorDescription = behaviorDescription
        }

        @Override
        void evaluate() throws Throwable {
            Statement updatedStatement = this.statement

            this.methodRules.each { rule -> updatedStatement = rule.apply(updatedStatement, method, specInstance) }
            this.testRules.each { rule -> updatedStatement = rule.apply(updatedStatement, behaviorDescription) }

            updatedStatement.evaluate()
        }

        private List<TestRule> getTestRules(Spec spec) {
            List<TestRule> rules = testClass.getAnnotatedFieldValues(spec, Rule.class, TestRule.class)
            rules.addAll(testClass.getAnnotatedMethodValues(spec, Rule.class, TestRule.class))

            return rules
        }

        private List<MethodRule> getMethodRules(Spec spec) {
            List<MethodRule> rules = testClass.getAnnotatedFieldValues(spec, Rule.class, MethodRule.class)
            rules.addAll(testClass.getAnnotatedMethodValues(spec, Rule.class, MethodRule.class))

            return rules
        }

        private FrameworkMethod getFrameworkMethod (Spec spec) {
            return new FrameworkMethod(getSpecMethod(spec))
        }
    }

    class AssumptionsStatement extends Statement {

        private Statement statement;
        private List<Closure> assumptions;

        public AssumptionsStatement (List<Closure> assumptions, Statement statement) {
            this.statement = statement
            this.assumptions = assumptions
        }

        @Override
        void evaluate() throws Throwable {
            this.assumptions.each { assumption ->
                println "Running assumption"
                assumption()
            }

            this.statement.evaluate()
        }
    }
}