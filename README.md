# Pine

Pine is a JUnit test runner that allows you to write specs in the style of RSpec and Jasmine. 

### Getting Started

Add both Pine and Groovy as test-time dependencies. Currently, Pine is only available as a standalone jar. If you're using Gradle, here's how to add it as a dependency via your `build.gradle` file:

```
dependencies {
	testCompile files('lib/pine-1.0-SNAPSHOT.jar')
}
```

### Writing Specs

Pine specs are just Groovy classes that adopt the trait `org.pine.Spec`. This trait provides your class with extra methods that allow you to write specs using the Pine DSL. 

Pine is a JUnit test runner, so Pine specs are really just a fancy way of writing JUnit tests. This means you can use JUnit-specific features in your tests. In particular, Pine supports the use of class and method rules, as well as the `@BeforeClass` and `@AfterClass` annotations. 

To use Pine as your test runner, annotate your test class with `@RunWith(SpecRunner)`

#### Pine Spec DSL

In Pine, specs are groups of behaviors that describe a component. Behaviors can be grouped into contexts. These contexts should be distinguished by the fact that certain specific conditions hold. Within each context, code can be run before or after each behavior. 

Use the following DSL to write your specs:

+ `describe(String name, Closure block)` - name your spec
+ `it(String name, Closure block)` - define a behavior
+ `when(String name, Closure block)` - define a context
+ `assume(Closure block)` - run before each behavior in the context
+ `finalize(Closure block)` -- run after each behavior in the context

You can focus a particular behavior by describing it using `fit` instead of `it`. If any behaviors in a spec are focused, only those behaviors will run when the spec is run. Note that this is true only within a spec class; behaviors defined in other classes will still run. 

### Examples

#### Groovy Script Style

Pine works best if you write your specs as Groovy scripts. To do so, you'll have to make sure your script extends a base class that implements the `Spec` trait using the `@BaseScript` annotation. Pine contains a convenience class for this purpose: `org.pine.script.SpecScript` that implements `Spec` and adds the appropriate `@RunWith(SpecRunner)` annotation. 

Here's a basic spec script:

```
@BaseScript SpecScript spec

describe 'My Component', {

	int someNumber = 0

    when 'something happens', {
    	assume {
    		someNumber = 2
    	}
    
        it 'runs a test', {
            assert someNumber == 2
        }

        it 'also does something else', {
            assert 1 == 1
        }
    }

}
```

Two annotations are provided to make writing specs with Groovy scripts easier. 

`@SpecScriptClass` -- Add this annotation to a class defined in your Groovy script and the other annotations belonging to that class will be transferred to the script class. This allows you to configure class-level annotations from within a script, without needing to introduce a new base class. 

```
@BaseScript SpecScript spec

@SpecScriptClass
@SomeClassAnnotation("with some value")
class SpecConfig {}

describe 'My Component', {

  it 'runs a test', {
    assert someNumber == 2
  }

}
```


`@SpecDelegate` -- Add this annotation to a field and the object belonging to this variable will be used as the delegate of the behavior closure. While this annotation can be used within any spec, it's especially useful within Groovy scripts. A Groovy script must inherit from the `Script` class, but sometimes it's useful when writing tests to extend from a different base class -- like when writing tests using Fluentlenium. Using the `@ScriptDelegate` annotation, you can call delegate method calls and property references to a class other than the base class. If a method call or property reference is not found in the delegate class, then the base class will be searched. 

```
@BaseScript SpecScript spec

@SpecDelegate @Field SomeObject object = new SomeObject()

describe 'My Component', {

  it 'runs a test', {
    assert someFunction() == 2
  }

}
```

where `someFunction()` is a method defined in `SomeObject`

#### Groovy Class Style

If you use a Groovy class to write a Pine spec, you should make sure the class implements `Spec` and annotate the method that contains the spec with `@Describe`.

```
@RunWith(SpecRunner)
public class MySpec implements Spec {

  @Describe("My Component")
  def spec() {
    
    it 'does something', {
      assert 1 == 1
    }
    
  }

}
```

### Example: Writing Spring Controller Tests

TBD

### Development

To run the tests:

```
$ ./gradlew clean test
```