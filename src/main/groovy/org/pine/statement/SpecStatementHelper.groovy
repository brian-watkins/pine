package org.pine.statement

import org.pine.Spec
import org.pine.SpecClass
import org.pine.annotation.SpecDelegate

public class SpecStatementHelper {
    public static void setDelegateForSpecClosure(SpecClass specClass, Spec specInstance, Closure block) {
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