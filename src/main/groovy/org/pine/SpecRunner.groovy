package org.pine

import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import org.junit.runners.ParentRunner
import org.junit.runners.model.InitializationError
import org.junit.runners.model.Statement
import org.junit.runners.model.TestClass
import org.pine.behavior.Behavior
import org.pine.statement.RulesStatement
import org.pine.statement.SpecAssumptionsStatement
import org.pine.util.SpecClass
import org.pine.visitor.SpecVisitor
import org.pine.visitor.SpecVisitorFactory

class SpecRunner extends ParentRunner<Behavior> {

    SpecClass specClass
    def behaviors = []

    SpecRunner(Class<?> testClass) throws InitializationError {
        super(testClass)

        this.specClass = (SpecClass) getTestClass()

        SpecVisitor specVisitor = visitSpec(getSpec())
        this.behaviors = specVisitor.getBehaviors()
    }

    protected TestClass createTestClass(Class testClass) {
        println "Creating spec class for runner from class: ${testClass.name}"
        return new SpecClass(testClass)
    }

    private Spec getSpec () {
        return (Spec) specClass.getSpecClass().newInstance()
    }

    private SpecVisitor visitSpec(Spec spec) {
        SpecVisitor specVisitor = SpecVisitorFactory.specVisitorForSpec(specClass)
        specVisitor.prepare(specClass, spec)
        spec.setSpecVisitor(specVisitor)
        specVisitor.getSpecMethod().invokeExplosively(spec)

        return specVisitor
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
        SpecVisitor specVisitor = visitSpec(spec)

        Behavior behavior = specVisitor.getBehaviorWithName(child.name)
        Description description = describeChild(behavior)

        println "Running behavior ..."

        Statement childStatement = behavior.createStatement()
        childStatement = new SpecAssumptionsStatement(specClass, spec, childStatement)
        childStatement = new RulesStatement(specClass, spec, specVisitor.getSpecMethod(), description, childStatement)

        runLeaf(childStatement, description, notifier)

        println "Done running behavior"
    }
}