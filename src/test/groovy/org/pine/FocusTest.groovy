package org.pine

import org.junit.Test
import org.junit.runner.RunWith
import org.pine.annotation.Describe
import org.pine.testHelpers.TestHelper

class FocusTest {

    @RunWith(SpecRunner)
    class FocusSpec implements Spec {

        @Describe def specScript () {

            fit 'should run this test', {
                assert 1 == 1
            }

            it 'should not run this test', {
                assert 1 == 0
            }

        }
    }

    @Test
    public void itRunsFocusedBehaviorsOnly () {
        TestHelper.assertSpecRuns(FocusSpec, 0, 1, 1, true);
    }

}
