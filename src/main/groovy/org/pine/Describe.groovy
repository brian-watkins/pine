package org.pine

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.*

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD])
@GroovyASTTransformationClass(["org.pine.DescribeAnnotationTransformation"])
public @interface Describe {
    String value()
}
