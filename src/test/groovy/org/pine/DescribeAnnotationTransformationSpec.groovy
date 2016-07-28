package org.pine

import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class DescribeAnnotationTransformationSpec {

    class DescribeSpec {
        @Describe("FunnyController")
        def spec () {

        }
    }

    @Test
    public void itAddsReadOnlySpecNamePropertyToClass() {
        DescribeSpec describeSpec = new DescribeSpec();

        assert describeSpec.specName == "FunnyController"
        assert describeSpec.getSpecName() == "FunnyController"

        shouldFail {
            describeSpec.setSpecName("can't do this")
        }

        shouldFail {
            describeSpec.specName = 'will not work'
        }
    }
}