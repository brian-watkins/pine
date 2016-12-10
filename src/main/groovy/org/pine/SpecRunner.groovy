package org.pine

import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import org.junit.runners.ParentRunner
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.InitializationError
import org.junit.runners.model.Statement
import org.junit.runners.model.TestClass
import org.pine.annotation.Describe
import org.pine.behavior.Behavior
import org.pine.statement.RulesStatement
import org.pine.statement.SpecAssumptionsStatement
import org.pine.exception.SpecNotFoundException
import org.pine.util.SpecClass
import org.pine.visitor.FeatureSpecVisitor
import org.pine.visitor.JourneySpecVisitor
import org.pine.visitor.SpecVisitor
import org.pine.visitor.SpecVisitorFactory

class SpecRunner extends ParentRunner<Behavior> {

    SpecClass specClass
    def behaviors = []

    public SpecRunner(Class<?> testClass) throws InitializationError {
        super(testClass)

        this.specClass = (SpecClass) getTestClass()

        Spec spec = getSpec()
        this.behaviors = spec.getBehaviors()
    }

    protected TestClass createTestClass(Class testClass) {
        println "Creating spec class for runner from class: ${testClass.name}"
        return new SpecClass(testClass)
    }

    public Spec getSpec() {
        Spec spec = (Spec) specClass.getSpecClass().newInstance()
        SpecVisitor specVisitor = SpecVisitorFactory.specVisitorForSpec(spec)
        specVisitor.visit(specClass)
        spec.setSpecVisitor(specVisitor)

        getSpecMethod(spec).invokeExplosively(spec)

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

    @Override
    protected List<Behavior> getChildren() {
        return behaviors
    }

    @Override
    protected Description describeChild(Behavior child) {
        return Description.createTestDescription(testClass.getName(), child.getName())
    }

    @Override
    protected void runChild(Behavior child, RunNotifier notifier) {
        if (!child.shouldRun()) {
            notifier.fireTestIgnored(describeChild(child))
            return
        }

        println "Getting spec to run behavior: ${child.name}"

        Spec spec = getSpec()

        println "Running behavior ..."

        Behavior behavior = spec.getBehaviors().find { b -> b.name == child.name }
        Description description = describeChild(behavior)

        Statement childStatement = behavior.createStatement(specClass, spec)
        childStatement = new SpecAssumptionsStatement(specClass, spec, childStatement)
        childStatement = new RulesStatement(specClass, spec, getSpecMethod(spec), description, childStatement)

        runLeaf(childStatement, description, notifier)

        println "Done running behavior"
    }
}