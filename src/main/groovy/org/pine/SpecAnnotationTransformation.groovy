package org.pine

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import java.util.stream.Collectors

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class SpecAnnotationTransformation extends AbstractASTTransformation {
    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        init(astNodes, sourceUnit)
        AnnotationNode specAnnotation = (AnnotationNode) astNodes[0];
        AnnotatedNode specNameVariable = (AnnotatedNode) astNodes[1];

        ClassNode parentClass = specNameVariable.declaringClass

        DeclarationExpression declarationExpression = (DeclarationExpression) astNodes[1]
        addProperty(parentClass, "specName", String.class, declarationExpression.getRightExpression())
    }

    private void addProperty(ClassNode cNode, String propertyName, Class propertyType, Expression initialValue) {
        FieldNode field = new FieldNode(
                propertyName,
                ACC_PRIVATE,
                new ClassNode(propertyType),
                new ClassNode(cNode.getClass()),
                initialValue
        );

        cNode.addProperty(new PropertyNode(field, ACC_PUBLIC, null, null));
    }
}