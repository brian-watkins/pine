package org.pine.statement

import org.pine.SpecClass
import org.pine.annotation.SpecDelegate

public class SpecStatementHelper {
    public static void setDelegateForSpecClosure(SpecClass specClass, Closure block) {
        Optional<Object> scriptDelegate = specClass.getAnnotatedFieldValues(block.getOwner(), SpecDelegate, Object).stream().findFirst()
        if (scriptDelegate.present) {
            println "Using spec delegate!"
            block.delegate = scriptDelegate.get()
            block.resolveStrategy = Closure.DELEGATE_FIRST
        } else {
            println "SpecDelegate not found!"
        }
    }
}