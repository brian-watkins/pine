# Pine

Pine is a JUnit test runner that allows you to write specs in the style of RSpec and Jasmine. 

### Getting Started

Currently, Pine is only available as a standalone jar. If you're using Gradle, here's how to add it as a dependency via your `build.gradle` file:

```
dependencies {
	testCompile files('lib/pine-1.0-SNAPSHOT.jar')
}
```

Also, add Groovy as a dependency and the Groovy Gradle plugin. There's a [helpful plugin](https://github.com/bwatkinsPivotal/pine-intellij-plugin) to run Pine specs using Gradle via Intellij.

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
+ `clean(Closure block)` - run after each behavior in the context

You can focus a particular behavior by describing it using `fit` instead of `it`. If any behaviors in a spec are focused, only those behaviors will run when the spec is run. Note that this is true only within a spec class; behaviors defined in other classes will still run. 

You can ignore a particular behavior by describing it using `xit` instead of `it`.

### Examples

#### Groovy Script Style

Pine works best if you write your specs as Groovy scripts. To do so, you'll have to make sure your script extends a base class that implements the `Spec` trait using the `@BaseScript` annotation. Pine contains a convenience class for this purpose: `org.pine.script.SpecScript` that implements `Spec` and adds the appropriate `@RunWith(SpecRunner)` annotation. 

Here's a basic spec script:

```
@BaseScript SpecScript spec

describe 'My Component', {

	int someNumber = 0

    when 'some number is 2', {
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


`@SpecDelegate` -- Add this annotation to a field and the object belonging to this variable will be used as the delegate of the script closures (`it` blocks, `assume` blocks, and `clean` blocks). While this annotation can be used within any spec, it's especially useful within Groovy scripts. A Groovy script must inherit from the `Script` class, but sometimes it's useful when writing tests to extend from a different base class -- like when writing tests using Fluentlenium. Using the `@ScriptDelegate` annotation, you can call delegate method calls and property references to a class other than the base class. If a method call or property reference is not found in the delegate class, then the base class will be searched.

```
@BaseScript SpecScript spec

@SpecDelegate @Field public SomeObject object = new SomeObject()

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

#### Other Helpful Annotations

Use `@Assume` to annotate methods that should be run before each spec, prior to any `assume` blocks. This can be useful when putting setup code in a script base class, for example. 


Note: methods annotated with `@Assume` do not work with `@SpecDelegate`.  

### Example: Writing Spring Controller Tests

Here's an example Spring controller test:

```
@BaseScript ControllerSpecBase specBase

@Autowired @Field
public FeedResourceRepository feedResourceRepository

describe 'FeedController', {
    when 'there is a feed with items', {
        FeedResource savedFeed

        assume {
            FeedResource feed = new FeedResource()
            feed.setFeedURL("http://my-fake-feed.com/feed.rss")
            feed.setName("Super Feed")
            savedFeed = feedResourceRepository.save(feed)
        }

        finalize {
            feedResourceRepository.deleteAll()
        }

        it 'returns the items', {
            mockMvc.perform(get("/feeds/" + savedFeed.getId() + "/items"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath('$', hasSize(1)))
                    .andExpect(jsonPath('$[0].title', equalTo("an item")))
                    .andExpect(jsonPath('$[0].description', equalTo("item description")))
        }
    }
}
```

and the referenced base script class:

```
@RunWith(SpecRunner)
@SpringBootTest(classes = [Application, TestConfig])
abstract class ControllerSpecBase extends Script implements Spec {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule()

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule()

    @Autowired
    ConfigurableWebApplicationContext context

    MockMvc mockMvc

    @Assume
    def setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

}
```

### Journey Specs

You can also write journey specs with Pine. Journey specs describe the
path a user might take through an app. In contrast with normal specs, 
journey specs does describe the system so much as the actions of a user. 
A journey spec passes if all the actions described were able to be completed. 

You write a journey spec with Pine by implementing the `JourneySpec` trait. 
It you are writing your specs in Groovy scripts, you can use the `org.pine.script.JourneySpecScript`
class as your base script. 

Here's an example:

```
@BaseScript JourneySpecScript spec

describe 'Holly accomplishes several tasks', {

    def nextTask

    she 'logs into the app', {
        doLogin()
    }

    she 'does the first task', {
        doSomeTask()
    }
    
    it 'shows the result', {
        assert 1 == 1
    }

    she 'goes to the next task', {
        goToTask(nextTask)
    }

    when 'she does task 2', {
    	assume {
            nextTask = "Have fun"
    	}
    
        she 'runs a test', {
            assert 1 == 1
        }
        
    }
    
    when 'she does task 3', {
        assume {
            nextTask = "Go Swimming"
        }

        she 'does something else', {
            assert 1 == 1
        }
    }
```

Notice a few things about this spec. 

First, journey specs describe a user's
journey through the system (not the behavior of a component), so 
the describe method should indicate the persona and the goal of that
persona's journey.
 
Second, journey specs can use `she` and `he` as well as `it` to describe 
behaviors or tasks. Use `she` or `he` when referring to what the user does
and `it` when describing how the system responds. 

Third, journey specs can have `assume` blocks. All assume blocks are
executed in order from outside to inside before any behavior blocks
are executed. Journey specs can also have `clean` blocks. These are
also executed in order from outside to inside, after all behavior
blocks have been executed. 

Finally, note that `when` blocks individuate journeys. So the above
example actually describes two journeys through the app, one which executes
the first `when` block and one which executes the second. Think of 
`when` blocks as branches leading to distinct journeys.  


### Development

To run the tests:

```
$ ./gradlew clean test
```