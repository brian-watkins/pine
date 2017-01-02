package org.pine.visitor

import org.pine.JourneySpec
import org.pine.Spec
import org.pine.util.SpecClass

class SpecVisitorFactory {

    static SpecVisitor specVisitorForSpec(SpecClass specClass) {
        if (isJourneySpec(specClass.specClass)) {
            print "Using Journey Spec Visitor"
            return new JourneySpecVisitor()
        }

        println "Using feature spec visitor"
        return new FeatureSpecVisitor()
    }

    static boolean isJourneySpec (Class specClass) {
        while (specClass.superclass != null) {
            if (specClass.interfaces.contains(JourneySpec)) {
                return true
            }

            specClass = specClass.superclass
        }

        return false
    }

}
