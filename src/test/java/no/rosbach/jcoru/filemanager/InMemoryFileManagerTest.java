package no.rosbach.jcoru.filemanager;

import static javax.tools.JavaFileObject.Kind;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

/**
 * Created by mapster on 09.03.15.
 */
public class InMemoryFileManagerTest {

  public static final InMemoryClassFile TEST_CLASS = new InMemoryClassFile("Test");
  private static final String PACKAGE = "package/sub";
  private static final String CLASS_NAME = "MyClass";
  private static final JavaSourceString JAVA_SOURCE = new JavaSourceString(CLASS_NAME + ".java", "");
  private static final JavaSourceString SOURCE_IN_PACKAGE = new JavaSourceString(PACKAGE + "/" + CLASS_NAME + ".java", "");
  private JavaFileManager systemFileManager;
  private InMemoryFileManager inMemoryFileManager;
  private HashMap<String, InMemoryClassFile> classStore;

  @Before
  public void setStage() throws IOException {
    systemFileManager = mock(JavaFileManager.class);
    when(
        systemFileManager.list(
            any(JavaFileManager.Location.class),
            anyString(),
            anySet(),
            anyBoolean())).thenReturn(Arrays.asList(new InMemoryClassFile("heisann")));

    inMemoryFileManager = new InMemoryFileManager(Arrays.asList(JAVA_SOURCE, SOURCE_IN_PACKAGE), Arrays.asList(TEST_CLASS), systemFileManager);
  }

  //
  //  getJavaFileForOutput
  //

  /**
   * Verify type when Location: CLASS_OUTPUT && sibling is a JavaSourceString
   *
   * @throws IOException never.
   */
  @Test
  public void getJavaFileForOutputShouldProvideInMemoryClassFileForClassOutputLocationAndJavaSourceStringSibling() throws IOException {
    JavaFileObject javaFileForOutput = inMemoryFileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, CLASS_NAME, Kind.CLASS, JAVA_SOURCE);
    assertTrue(ManagedFileObject.getWrappedObject(javaFileForOutput) instanceof InMemoryClassFile);
  }

  /**
   * Verify uri when Location: CLASS_OUTPUT && sibling is a JavaSourceString
   *
   * @throws IOException never.
   */
  @Test
  public void getJavaFileForOutputShouldProvideFileObjectWithClassNameAsURI() throws IOException {
    JavaFileObject javaFileForOutput = inMemoryFileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, CLASS_NAME, Kind.CLASS, JAVA_SOURCE);
    assertEquals(URI.create(CLASS_NAME), javaFileForOutput.toUri());
  }

  /**
   * Verify that objects are wrapped as ManagedFileObject
   */
  @Test
  public void getJavaFileForOutputShouldProvideManagedFileObjects() throws IOException {
    JavaFileObject javaFileForOutput = inMemoryFileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, CLASS_NAME, Kind.CLASS, JAVA_SOURCE);
    assertTrue(javaFileForOutput instanceof ManagedFileObject);
  }

  //
  //  handleOption
  //
  @Test
  public void shouldDefer() {
    Iterator<String> args = Arrays.asList("junit-4.11.jar").iterator();
    inMemoryFileManager.handleOption("-classpath", args);
    verify(systemFileManager).handleOption("-classpath", args);
  }

  //
  //  hasLocation
  //

  @Test
  public void hasLocationShouldClaimClassOutputLocation() {
    assertTrue(inMemoryFileManager.hasLocation(StandardLocation.CLASS_OUTPUT));
  }

  @Test
  public void hasLocationShouldClaimClassPathLocation() {
    assertTrue(inMemoryFileManager.hasLocation(StandardLocation.CLASS_PATH));
  }

  @Test
  public void hasLocationShouldClaimSourcePathLocation() {
    assertTrue(inMemoryFileManager.hasLocation(StandardLocation.SOURCE_PATH));
  }

  @Test
  public void hasLocationShouldClaimSourceOutputLocation() {
    assertTrue(inMemoryFileManager.hasLocation(StandardLocation.SOURCE_OUTPUT));
  }

  @Test
  public void hasLocationShouldDeferAnnotationProcessorPathLocation() {
    inMemoryFileManager.hasLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH);
    verify(systemFileManager).hasLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH);
  }

  @Test
  public void hasLocationShouldDeferPlatformClassPathLocation() {
    inMemoryFileManager.hasLocation(StandardLocation.PLATFORM_CLASS_PATH);
    verify(systemFileManager).hasLocation(StandardLocation.PLATFORM_CLASS_PATH);
  }


  //
  //  inferBinaryName
  //

  @Test
  public void inferBinaryNameShouldDeferPlatformClassPathToSystemFileManager() {
    JavaSourceString javaObject = new JavaSourceString("", "");
    inMemoryFileManager.inferBinaryName(StandardLocation.PLATFORM_CLASS_PATH, javaObject);
    verify(systemFileManager).inferBinaryName(StandardLocation.PLATFORM_CLASS_PATH, javaObject);
  }

  @Test
  public void inferBinaryNameShouldDeferAnnotationProcessorPathToSystemFileManager() {
    JavaSourceString javaObject = new JavaSourceString("", "");
    inMemoryFileManager.inferBinaryName(StandardLocation.ANNOTATION_PROCESSOR_PATH, javaObject);
    verify(systemFileManager).inferBinaryName(StandardLocation.ANNOTATION_PROCESSOR_PATH, javaObject);
  }

  @Test
  public void inferBinaryNameForSourcePathShouldReturnURI() {
    JavaSourceString javaObject = new JavaSourceString("fixtures/MyClass.java", "");
    assertEquals(javaObject.toUri().toString(), inMemoryFileManager.inferBinaryName(StandardLocation.SOURCE_PATH, javaObject));
  }

  @Test
  public void inferBinaryNameShouldDeferManagedFileObjects() {
    inMemoryFileManager.inferBinaryName(StandardLocation.SOURCE_PATH, new ManagedFileObject(systemFileManager, JAVA_SOURCE));
    verify(systemFileManager).inferBinaryName(StandardLocation.SOURCE_PATH, JAVA_SOURCE);
  }

  @Test
  public void inferBinaryNameShouldReplaceSlashAndDotJavaForSourceKind() {
    String infered = inMemoryFileManager.inferBinaryName(StandardLocation.CLASS_OUTPUT, SOURCE_IN_PACKAGE);
    assertEquals(SOURCE_IN_PACKAGE.toUri().toString().replace("/", ".").replace(".java", ""), infered);
  }
  //
  //  list
  //

  @Test
  public void listShouldDeferPlatformClassPathToSystemFileManager() throws IOException {
    inMemoryFileManager.list(StandardLocation.PLATFORM_CLASS_PATH, "", set(Kind.CLASS), false);
    verify(systemFileManager).list(StandardLocation.PLATFORM_CLASS_PATH, "", set(Kind.CLASS), false);
  }

  @Test
  public void listShouldDeferAnnotationProcessorPathToSystemFileManager() throws IOException {
    inMemoryFileManager.list(StandardLocation.ANNOTATION_PROCESSOR_PATH, "", set(Kind.CLASS), false);
    verify(systemFileManager).list(StandardLocation.ANNOTATION_PROCESSOR_PATH, "", set(Kind.CLASS), false);
  }

  @Test
  public void listShouldReturnSourcesInRoot() {
    List<JavaFileObject> sources = list(StandardLocation.SOURCE_PATH, "", set(Kind.SOURCE), false);
    assertEquals(1, sources.size());
    assertSame(JAVA_SOURCE, sources.get(0));
  }

  @Test
  public void listShouldReturnSourcesInPackage() {
    List<JavaFileObject> sources = list(StandardLocation.SOURCE_PATH, PACKAGE.replace("/", "."), set(Kind.SOURCE), false);
    assertEquals(1, sources.size());
    assertSame(SOURCE_IN_PACKAGE, sources.get(0));
  }

  @Test
  public void listShouldReturnAllSourcesWithRecurse() {
    List<JavaFileObject> sources = list(StandardLocation.SOURCE_PATH, "", set(Kind.SOURCE), true);
    assertEquals(2, sources.size());
  }

  @Test
  public void listShouldWrapSystemManagedFileObjects() {
    List<JavaFileObject> list = list(StandardLocation.PLATFORM_CLASS_PATH, "java.util", set(Kind.CLASS, Kind.CLASS), false);
    assertTrue(list.stream().allMatch(file -> file instanceof ManagedFileObject));
  }

  @Test
  public void listShouldReturnFromClassPathLocation() throws IOException {
    // stage
    final InMemoryClassFile myClass = new InMemoryClassFile("MyClass");
    inMemoryFileManager.addClassPathClass(myClass);

    // act
    List<JavaFileObject> list = list(StandardLocation.CLASS_PATH, "", set(Kind.CLASS), false);
    assertTrue(list.contains(myClass));
  }

  @Test
  public void listShouldReturnFromClassPathLocationWhenClassIsAddedAfterConstruction() {
    // stage
    final InMemoryClassFile newClass = new InMemoryClassFile("NewClass");
    inMemoryFileManager.addClassPathClass(newClass);

    //act
    List<JavaFileObject> list = list(StandardLocation.CLASS_PATH, "", set(Kind.CLASS), false);
    assertTrue(list.contains(newClass));
  }

  @Test
  public void listShouldReturnFromClassPathLocationWhenClassIsInPackage() {
    // stage
    final InMemoryClassFile newClass = new InMemoryClassFile("mypackage.NewClass");
    inMemoryFileManager.addClassPathClass(newClass);

    // act
    List<JavaFileObject> list = list(StandardLocation.CLASS_PATH, "mypackage", set(Kind.CLASS), false);
    assertTrue(list.contains(newClass));
  }


  @Test
  public void listShouldReturnFromClassOutputLocation() throws IOException {
    // act
    List<JavaFileObject> list = list(StandardLocation.CLASS_OUTPUT, "", set(Kind.CLASS), false);
    assertTrue(list.contains(TEST_CLASS));
  }

  @Test
  public void listShouldReturnFromClassOutputLocationWhenClassIsAddedAfterConstruction() {
    final InMemoryClassFile newClass = new InMemoryClassFile("NewClass");
    inMemoryFileManager.addOutputClass(newClass);

    // act
    List<JavaFileObject> list = list(StandardLocation.CLASS_OUTPUT, "", set(Kind.CLASS), false);
    assertTrue(list.contains(newClass));
  }

  @Test
  public void listShouldReturnFromClassOutputLocationWhenClassIsInPackage() {
    // stage
    final InMemoryClassFile newClass = new InMemoryClassFile("mypackage.NewClass");
    inMemoryFileManager.addOutputClass(newClass);

    // act
    List<JavaFileObject> list = list(StandardLocation.CLASS_OUTPUT, "mypackage", set(Kind.CLASS), false);
    assertTrue(list.contains(newClass));
  }

  private <T> Set<T> set(T... elements) {
    return new HashSet<>(Arrays.asList(elements));
  }

  private List<JavaFileObject> list(StandardLocation location, String path, Set<Kind> kinds, boolean recurse) {
    try {
      List<JavaFileObject> list = new LinkedList<>();
      for (JavaFileObject file : inMemoryFileManager.list(location, path, kinds, recurse)) {
        list.add(file);
      }
      return list;
    } catch (IOException e) {
      throw new Error("Failed to list files.", e);
    }
  }
}
