package org.pine.statement

import org.junit.runners.model.Statement
import org.pine.Spec
import org.pine.util.SpecClass
import org.pine.annotation.SpecDelegate

abstract class SpecStatement extends Statement {

    SpecClass specClass
    Spec specInstance

    public SpecStatement (SpecClass specClass, Spec specInstance) {
        this.specClass = specClass
        this.specInstance = specInstance
    }

    protected void setDelegateForSpecClosure(Closure block) {
        Optional<Object> scriptDelegate = specClass.getAnnotatedFieldValues(specInstance, SpecDelegate, Object).stream().findFirst()
        if (scriptDelegate.present) {
            println "Using spec delegate!"
            block.delegate = scriptDelegate.get()
            block.resolveStrategy = Closure.DELEGATE_FIRST
        } else {
            println "SpecDelegate not found!"
        }
    }
}
