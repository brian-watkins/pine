import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.pine.annotation.SpecDelegate

beforeVisitClass { classNode ->
    ClassNode node = classNode

    while (node != null) {
        FieldNode delegateField = node.getFields().find { field ->
            isAnnotatedBy(field, SpecDelegate)
        }

        if (delegateField) {
            delegatesTo(delegateField.getType())
            break
        }

        node = node.getSuperClass()
    }
}
