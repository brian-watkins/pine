package org.pine

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class DescribeAnnotationTransformation extends AbstractASTTransformation {
    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        init(astNodes, sourceUnit)
        AnnotationNode describeAnnotation = (AnnotationNode) astNodes[0];
        AnnotatedNode specMethod = (AnnotatedNode) astNodes[1];

        ClassNode parentClass = specMethod.declaringClass

        addProperty(parentClass, "specName", String.class, describeAnnotation.getMember("value"))
    }

    private void addProperty(ClassNode cNode, String propertyName, Class propertyType, Expression initialValue) {
        FieldNode field = new FieldNode(
                propertyName,
                ACC_PUBLIC | ACC_FINAL,
                new ClassNode(propertyType),
                new ClassNode(cNode.getClass()),
                initialValue
        );

        cNode.addProperty(new PropertyNode(field, ACC_PUBLIC | ACC_FINAL, null, null));
    }
}