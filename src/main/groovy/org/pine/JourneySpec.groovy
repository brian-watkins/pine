package org.pine

import org.pine.block.ContextBlock

trait JourneySpec extends Spec {

    def describe(String personaName, String journeyName, Closure block) {
        println "Persona: ${personaName}"
        println "Describe ${journeyName}"

        this.metaClass."${personaName}" << { name, exampleBlock -> it(name, exampleBlock) }

        specVisitor.visitRootContext(new ContextBlock("${personaName} ${journeyName}"))
        block(this.&it)
    }

}