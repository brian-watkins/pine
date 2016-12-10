package org.pine.behavior

import org.junit.runners.model.Statement
import org.pine.Spec
import org.pine.util.SpecClass

public interface Behavior {

    boolean shouldRun()
    public Statement createStatement(SpecClass specClass, Spec specInstance)
    public String getName()

}
