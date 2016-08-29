package org.pine

import org.junit.Test
import org.junit.runner.RunWith
import org.pine.annotation.Describe
import org.pine.testHelpers.TestHelper

class IgnoreTest {

    @RunWith(SpecRunner)
    class IgnoreSpec implements Spec {

        @Describe
        def specScript() {

            it 'should run this test', {
                assert 1 == 1
            }

            xit 'should not run this test', {
                assert 1 == 0
            }

        }
    }

    @Test
    public void itIgnoresIgnoredBehaviors() {
        TestHelper.assertSpecRuns(IgnoreSpec, 0, 1, 1, true);
    }

}
