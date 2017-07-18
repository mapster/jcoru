package no.rosbach.jcoru.compile;

import no.rosbach.jcoru.JcoruApplication;
import no.rosbach.jcoru.compile.fixtures.*;
import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.filemanager.InMemoryFileManager;
import no.rosbach.jcoru.provider.JavaCompilerProvider;
import no.rosbach.jcoru.provider.WhitelistProvider;
import no.rosbach.jcoru.security.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static no.rosbach.jcoru.utils.Stream.stream;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransientClassLoaderTest {
  private TransientClassLoader classLoader;
  private Class<TestClass> loadedTestClass;

  private WhitelistProvider whitelistProvider = new WhitelistProvider();
  @Resource
  private JavaCompileUtil compiler;
  @Resource
  private InMemoryFileManager inMemoryFileManager;

  @Configuration
  @Import(JcoruApplication.class)
  public static class TestConfiguration {
    @Bean
    public AccessManager<String> classLoaderWhitelist() {
      return getWhitelistWithExtra("no.rosbach.jcoru.compile.fixtures.*");
    }
  }

  @Before
  public void setStage() throws IOException {
    Fixtures[] fixtures = {Fixtures.TEST_CLASS, Fixtures.AGGREGATION_CLASS, Fixtures.CONTAINED_CLASS};

    // First compile fixture interfaces to make them available as java byte code classes.
    JavaCompileUtil otherCompiler = createNewCompiler();
    List<CompiledClassObject> compiledInterfaces = otherCompiler.compile(
        stream(fixtures).map(Fixtures::getFixtureInterfaceSource).collect(toList()),
        new SensitiveDiagnosticListener());

    // Add compiled interfaces to file manager and compile sources
    compiledInterfaces.forEach(f -> inMemoryFileManager.addClassPathClass(f));
    compiler.compile(Fixtures.getFixtureSources(fixtures), new SensitiveDiagnosticListener());

    classLoader = (TransientClassLoader) compiler.getClassLoader();
    loadedTestClass = loadClass(TestClass.class);
  }

  private JavaCompileUtil createNewCompiler() {
    JavaCompilerProvider javaCompilerProvider = new JavaCompilerProvider();
    return new JavaCompileUtil(
            javaCompilerProvider.javaCompiler(),
            new InMemoryFileManager(
                    new TransientClassLoader(whitelistProvider.classLoaderWhitelist()),
                    whitelistProvider.fileManagerPackageWhitelist(),
                    javaCompilerProvider.systemFileManager()
            )
    );
  }

  @Test
  public void ableToLoadClass() {
    assertEquals(TestClass.class.getSimpleName(), loadedTestClass.getName());
    assertTrue(classLoader.isClassLoaded(loadedTestClass.getName()));
  }

  @Test
  public void returnsSameClassInstanceForSecondAttemptAtLoadClass() throws ClassNotFoundException {
    Class<TestClass> first = loadClass(TestClass.class);
    Class<TestClass> second = loadClass(TestClass.class);
    assertSame(first, second);
  }

  @Test
  public void shouldNotStateAnUnloadedClassIsLoaded() {
    assertFalse(classLoader.isClassLoaded("NotLoadedClass"));
  }

  @Test
  public void ableToCreateInstanceOfLoadedClass() {
    TestClass instance = createInstance(loadedTestClass);
    assertEquals(TestClass.class.getSimpleName(), instance.getClass().getName());
  }

  @Test
  public void ableToInvokeMethod() {
    assertEquals(TestClass.testResult, createInstance(loadedTestClass).test());
  }

  @Test
  public void ableToLoadAggregationClass() {
    Class<AggregationClass> clazz = loadClass(AggregationClass.class);
    assertEquals(AggregationClass.class.getSimpleName(), clazz.getName());
    assertTrue(classLoader.isClassLoaded(clazz.getName()));
  }

  @Test
  public void containedClassIsLoadedWhenInstanceOfAggregationClassIsCreated() {
    Class<AggregationClass> clazz = loadClass(AggregationClass.class);
    AggregationClass instance = createInstance(clazz);
    assertTrue(classLoader.isClassLoaded(instance.getValue().getClass().getName()));
  }

  @Test
  public void ableToInvokeMethodOnAggregationClass() {
    Class<AggregationClass> clazz = loadClass(AggregationClass.class);
    assertEquals(ContainedClass.actualValue, createInstance(clazz).getContainedValue());
  }

  @Test
  public void ableToGetReferencedContainedClass() {
    Class<AggregationClass> clazz = loadClass(AggregationClass.class);
    System.out.println(AggregationClass.class.getClassLoader());
    System.out.println(createInstance(clazz).getClass().getClassLoader());
    AggregationClass instance = createInstance(clazz);
    assertEquals(ContainedClass.class.getSimpleName(), instance.getValue().getClass().getName());
    assertEquals(ContainedClass.actualValue, instance.getValue().getActualValue());
  }

  @Test
  public void newClassLoaderShouldBeAbleToLoadPreviouslyLoadedClass() {
    // stage
    classLoader = new TransientClassLoader(getWhitelistWithExtra("no.rosbach.jcoru.compile.fixtures.TestClass"));
    classLoader.setInMemoryFileManager(compiler.getInMemoryFileManager());

    // act
    Class<TestClass> newClass = loadClass(TestClass.class);
    assertNotSame(loadedTestClass, newClass);
  }

  @Test(expected = StrictAccessControlException.class)
  public void classLoaderRespectsWhitelist() {
    // stage
    classLoader = new TransientClassLoader(new WhitelistAccessManager(new HashSet<>()));
    classLoader.setInMemoryFileManager(compiler.getInMemoryFileManager());

    // act
    loadClass(TestClass.class);
  }

  private <C> Class<C> loadClass(Class<C> classInterface) {
    try {
      Class<C> cClass = (Class<C>) classLoader.loadClass(classInterface.getSimpleName());
      return cClass;
    } catch (ClassNotFoundException e) {
      throw new Error("Could not find class.", e);
    }
  }

  private <C> C createInstance(Class<C> clazz) {
    try {
      Constructor<C> constructor = clazz.getConstructor();
      return constructor.newInstance();
    } catch (Exception e) {
      throw new Error("Unable to create instance.", e);
    }
  }

  static AccessManager<String> getWhitelistWithExtra(String... entry) {
    return new WhitelistProvider().classLoaderWhitelist().extend(new HashSet<>(Arrays.asList(entry)));
  }
}
