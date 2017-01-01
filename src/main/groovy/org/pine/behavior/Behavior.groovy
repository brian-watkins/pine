package org.pine.behavior

import org.junit.runners.model.Statement

interface Behavior {

    boolean shouldRun()
    Statement createStatement()
    String getName()

}
