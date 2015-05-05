package no.rosbach.jcoru.compile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import no.rosbach.jcoru.compile.fixtures.AggregationClass;
import no.rosbach.jcoru.compile.fixtures.ContainedClass;
import no.rosbach.jcoru.compile.fixtures.Fixtures;
import no.rosbach.jcoru.compile.fixtures.TestClass;
import no.rosbach.jcoru.filemanager.InMemoryClassFile;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;

/**
 * Created by mapster on 14.03.15.
 */
public class TransientClassLoaderTest {

  private TransientClassLoader classLoader;
  private Class<TestClass> loadedTestClass;
  private HashMap<String, InMemoryClassFile> classStore;

  @Before
  public void setStage() throws IOException {
    classStore = new HashMap<>();
    JavaCompileUtil compiler = new JavaCompileUtil(new SensitiveDiagnosticListener());
    compiler.compile(Fixtures.getFixtureAndInterfaceSources(Fixtures.TEST_CLASS, Fixtures.AGGREGATION_CLASS, Fixtures.CONTAINED_CLASS))
        .stream().forEach(clazz -> classStore.put(clazz.getName(), (InMemoryClassFile) clazz.getWrappedObject()));

    classLoader = new TransientClassLoader(classStore);

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
  public void newClassLoadedShouldBeAbleToLoadPreviouslyLoadedClass() {
    classLoader = new TransientClassLoader(classStore);
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
