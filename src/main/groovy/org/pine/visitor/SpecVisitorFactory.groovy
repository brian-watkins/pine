package org.pine.visitor

import org.pine.JourneySpec
import org.pine.Spec

class SpecVisitorFactory {

    public static SpecVisitor specVisitorForSpec(Spec specInstance) {
        if (specInstance instanceof JourneySpec) {
            print "Using Journey Spec Visitor"
            return new JourneySpecVisitor()
        }

        println "Using feature spec visitor"
        return new FeatureSpecVisitor()
    }

}
