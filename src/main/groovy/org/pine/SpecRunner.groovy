package org.pine

import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import org.junit.runners.ParentRunner
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.InitializationError
import org.junit.runners.model.Statement
import org.junit.runners.model.TestClass
import org.pine.annotation.Describe
import org.pine.statement.AssumptionsStatement
import org.pine.statement.BehaviorStatement
import org.pine.statement.CleanStatement
import org.pine.statement.RulesStatement

class SpecRunner extends ParentRunner<Behavior> {

    SpecClass specClass
    def behaviors = []
    private boolean hasFocusedBehaviors = false

    public SpecRunner(Class<?> testClass) throws InitializationError {
        super(testClass)

        this.specClass = (SpecClass) getTestClass()

        Spec spec = getSpec()
        hasFocusedBehaviors = spec.hasFocusedBehaviors
        this.behaviors = spec.getBehaviors()
    }

    protected TestClass createTestClass(Class testClass) {
        println "Creating spec class for runner from class: ${testClass.name}"
        return new SpecClass(testClass)
    }

    public Spec getSpec() {
        Spec spec = (Spec) specClass.getSpecClass().newInstance()

        getSpecMethod(spec).invokeExplosively(spec)

        if (spec.specName == null) {
            setSpecName(spec)
        }

        return spec
    }

    private FrameworkMethod getSpecMethod(Spec spec) {
        if (spec instanceof Script) {
            return new FrameworkMethod(spec.class.getMethod("run", null))
        }

        return specClass.getAnnotatedMethods(Describe).stream()
                .findFirst()
                .orElseThrow(SpecNotFoundException.metaClass.&invokeConstructor)
    }

    private void setSpecName (Spec spec) {
        FrameworkMethod specMethod = testClass.getAnnotatedMethods(Describe).stream()
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
        if (child.isIgnored() || ( hasFocusedBehaviors && !child.isFocused() )) {
            notifier.fireTestIgnored(describeChild(child))
            return
        }

        println "Getting spec to run behavior: ${child.name}"

        Spec spec = getSpec()

        println "Running behavior ..."

        Behavior behavior = spec.behaviors.find{ b -> b.name == child.name }
        Description description = describeChild(behavior)

        Statement childStatement = new CleanStatement(specClass, spec, behavior.cleaners)
        childStatement = new BehaviorStatement(specClass, spec, behavior, childStatement)
        childStatement = new AssumptionsStatement(specClass, spec, behavior.assumptions, childStatement)
        childStatement = new RulesStatement(specClass, spec, getSpecMethod(spec), description, childStatement)

        runLeaf(childStatement, description, notifier)

        println "Done running behavior"
    }
}