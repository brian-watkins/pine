package org.pine

import org.junit.Test
import org.pine.testHelpers.FunAnnotation

class SpecClassTest {

    @Test
    public void itGetsTheSpecClassName () {
        SpecClass specClass = new SpecClass(MyTestSpecScriptClass.class)

        assert specClass.getName() == 'org.pine.SpecClassTest$MyTestSpecScriptClass'
    }

    @Test
    public void itGetsTheAnnotatedStaticFields () {
        SpecClass specClass = new SpecClass(MyTestSpecScriptClass.class)

        assert specClass.getAnnotatedFieldValues(null, FunAnnotation.class, String.class).size() == 1
    }

    @Test
    public void itGetsTheAnnotatedInstanceFields () {
        SpecClass specClass = new SpecClass(MyTestSpecScriptClass.class)

        MyTestSpecScriptClass instance = new MyTestSpecScriptClass()

        assert specClass.getAnnotatedFieldValues(instance, FunAnnotation.class, String.class).size() == 2
    }

    @Test
    public void itGetsTheAnnotatedStaticMethods () {
        SpecClass specClass = new SpecClass(MyTestSpecScriptClass.class)

        assert specClass.getAnnotatedMethodValues(null, FunAnnotation.class, Integer.class).size() == 1
    }

    @Test
    public void itGetsTheAnnotatedInstanceMethods () {
        SpecClass specClass = new SpecClass(MyTestSpecScriptClass.class)

        MyTestSpecScriptClass instance = new MyTestSpecScriptClass()

        assert specClass.getAnnotatedMethodValues(instance, FunAnnotation.class, String.class).size() == 1
    }

    class MyTestSpecScriptClass {

        @FunAnnotation
        public static String ANNOTATED_STATIC_STRING = "awesome"

        @FunAnnotation
        public String funType = "running"

        @FunAnnotation
        public static Integer getFunStuff() {
            return new Integer(1)
        }

        @FunAnnotation
        public String getOtherFunThings() {
            return "otherFunThings"
        }
    }
}
