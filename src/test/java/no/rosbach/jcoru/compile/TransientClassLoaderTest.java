package no.rosbach.jcoru.compile;

import static java.util.stream.Collectors.toList;
import static no.rosbach.jcoru.utils.Stream.stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import no.rosbach.jcoru.compile.fixtures.AggregationClass;
import no.rosbach.jcoru.compile.fixtures.ContainedClass;
import no.rosbach.jcoru.compile.fixtures.Fixtures;
import no.rosbach.jcoru.compile.fixtures.TestClass;
import no.rosbach.jcoru.factory.JavaCompilerProvider;
import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.filemanager.InMemoryClassFile;
import no.rosbach.jcoru.filemanager.InMemoryFileManager;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import javax.tools.ToolProvider;

/**
 * Created by mapster on 14.03.15.
 */
public class TransientClassLoaderTest {
  private JavaCompilerProvider provider = new JavaCompilerProvider();
  private TransientClassLoader classLoader;
  private Class<TestClass> loadedTestClass;
  private JavaCompileUtil compiler;

  @Before
  public void setStage() throws IOException {
    Fixtures[] fixtures = {Fixtures.TEST_CLASS, Fixtures.AGGREGATION_CLASS, Fixtures.CONTAINED_CLASS};

    // First compile fixture interfaces to make them available as java byte code classes.
    compiler = new JavaCompileUtil(
        ToolProvider.getSystemJavaCompiler(),
        new InMemoryFileManager(new TransientClassLoader(), provider.getFileManagerPackagesWhitelist()));
    List<CompiledClassObject> compiledInterfaces = compiler.compile(
        stream(fixtures).map(Fixtures::getFixtureInterfaceSource).collect(toList()),
        new SensitiveDiagnosticListener());

    // Then compile the fixtures to test.
    InMemoryFileManager fileManager = new InMemoryFileManager(new TransientClassLoader(), provider.getFileManagerPackagesWhitelist());
    compiler = new JavaCompileUtil(ToolProvider.getSystemJavaCompiler(), fileManager);
    compiledInterfaces.forEach(f -> fileManager.addClassPathClass(f));
    compiler.compile(Fixtures.getFixtureSources(fixtures), new SensitiveDiagnosticListener());

    classLoader = (TransientClassLoader) compiler.getClassLoader();
    loadedTestClass = loadClass(TestClass.class);
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
    AggregationClass instance = createInstance(clazz);
    assertEquals(ContainedClass.class.getSimpleName(), instance.getValue().getClass().getName());
    assertEquals(ContainedClass.actualValue, instance.getValue().getActualValue());
  }

  @Test
  public void newClassLoaderShouldBeAbleToLoadPreviouslyLoadedClass() {
    classLoader = new TransientClassLoader(compiler.getFileManager());
    Class<TestClass> newClass = loadClass(TestClass.class);
    assertNotSame(loadedTestClass, newClass);
  }

  private Object invoke(Method method, Object instance) {
    try {
      return method.invoke(instance);
    } catch (Exception e) {
      throw new Error("Unable to invoke method.", e);
    }
  }

  private void addClass(HashMap<String, InMemoryClassFile> classStore, Class<?> clazz) throws IOException {
    classStore.put(
        clazz.getSimpleName(),
        new InMemoryClassFile(
            URI.create(clazz.getSimpleName()),
            this.getClass().getClassLoader().getResourceAsStream(clazz.getSimpleName() + ".class")));
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

  private Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
    try {
      return clazz.getMethod(name, parameterTypes);
    } catch (NoSuchMethodException e) {
      throw new Error("Unable to get method.", e);
    }
  }
}
