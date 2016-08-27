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
import org.pine.annotation.Describe
import org.pine.annotation.SpecDelegate

import java.lang.reflect.Method

class SpecRunner extends ParentRunner<Behavior> {

    Class specClass
    def behaviors = []
    private boolean hasFocusedBehaviors = false

    public SpecRunner(Class<?> testClass) throws InitializationError {
        super(testClass)

        this.specClass = testClass

        Spec spec = getSpec()
        hasFocusedBehaviors = spec.hasFocusedBehaviors
        this.behaviors = spec.getBehaviors()
    }

    protected TestClass createTestClass(Class testClass) {
        println "Creating spec class for runner from class: ${testClass.name}"
        return new SpecClass(testClass)
    }

    public Spec getSpec() {
        Spec spec = (Spec) specClass.newInstance()

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
        return Description.createTestDescription(testClass.getName(), child.getDisplayName())
    }

    @Override
    protected void runChild(Behavior child, RunNotifier notifier) {
        if (hasFocusedBehaviors && !child.focused) {
            notifier.fireTestIgnored(describeChild(child))
            return
        }

        println "Getting spec to run behavior: ${child.name}"

        Spec spec = getSpec()

        println "Running behavior ..."

        Behavior behavior = spec.behaviors.find{ b -> b.name == child.name }
        Description description = describeChild(behavior)

        Statement childStatement = new FinalizerStatement(behavior.finalizers)
        childStatement = new BehaviorStatement(spec, behavior, childStatement)
        childStatement = new AssumptionsStatement(behavior.assumptions, childStatement)
        childStatement = new RulesStatement(spec, description, childStatement)

        runLeaf(childStatement, description, notifier)

        println "Done running behavior"
    }

    class BehaviorStatement extends Statement {

        private Statement statement
        private Behavior behavior
        private Spec spec

        public BehaviorStatement (Spec spec, Behavior behavior, Statement statement) {
            this.statement = statement
            this.behavior = behavior
            this.spec = spec
            setBehaviorDelegate()
        }

        @Override
        void evaluate() throws Throwable {
            println "Running behavior block"
            behavior.block()

            this.statement.evaluate()
        }

        private void setBehaviorDelegate() {
            Optional<Object> scriptDelegate = testClass.getAnnotatedFieldValues(this.spec, SpecDelegate, Object).stream().findFirst()
            if (scriptDelegate.present) {
                behavior.block.delegate = scriptDelegate.get()
                behavior.block.resolveStrategy = Closure.DELEGATE_FIRST
            }
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

    class FinalizerStatement extends Statement {

        private List<Closure> finalizers

        public FinalizerStatement (List<Closure> finalizers) {
            this.finalizers = finalizers
        }

        @Override
        void evaluate() throws Throwable {
            this.finalizers.forEach { block ->
                println "Running finalizer"
                block()
            }
        }
    }
}