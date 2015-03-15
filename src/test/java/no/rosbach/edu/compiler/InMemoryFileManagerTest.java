package no.rosbach.edu.compiler;

import org.junit.Before;
import org.junit.Test;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static javax.tools.JavaFileObject.Kind;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mapster on 09.03.15.
 */
public class InMemoryFileManagerTest {

    private static final String PACKAGE = "package/sub";
    private static final String CLASS_NAME = "MyClass";
    private static final JavaSourceString JAVA_SOURCE = new JavaSourceString(CLASS_NAME + ".java", "");
    private static final JavaSourceString SOURCE_IN_PACKAGE = new JavaSourceString(PACKAGE + "/" + CLASS_NAME + ".java", "");
    private JavaFileManager systemFileManager;
    private InMemoryFileManager inMemoryFileManager;

    @Before
    public void setStage() {
        systemFileManager = mock(JavaFileManager.class);
        inMemoryFileManager = new InMemoryFileManager(Arrays.asList(JAVA_SOURCE, SOURCE_IN_PACKAGE), new HashMap<String, InMemoryClassFile>(), systemFileManager);
    }

    //
    //  getJavaFileForOutput
    //

    /**
     * Verify uri when Location: CLASS_OUTPUT && sibling is a JavaSourceString
     * @throws IOException never.
     */
    @Test
    public void getJavaFileForOutputShouldProvideInMemoryClassFileForClassOutputLocationAndJavaSourceStringSibling () throws IOException {
        JavaFileObject javaFileForOutput = inMemoryFileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, CLASS_NAME, Kind.CLASS, JAVA_SOURCE);
        assertTrue(javaFileForOutput instanceof InMemoryClassFile);
    }

    /**
     * Verify uri when Location: CLASS_OUTPUT && sibling is a JavaSourceString
     * @throws IOException never.
     */
    @Test
    public void getJavaFileForOutputShouldProvideFileObjectWithClassNameAsURI() throws IOException {
        JavaFileObject javaFileForOutput = inMemoryFileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, CLASS_NAME, Kind.CLASS, JAVA_SOURCE);
        assertEquals(URI.create(CLASS_NAME), javaFileForOutput.toUri());
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

    private <T> Set<T> set(T... elements) {
        return new HashSet<T>(Arrays.asList(elements));
    }

    private List<JavaFileObject> list(StandardLocation location, String path, Set<Kind> kinds, boolean recurse) {
        try {
            List<JavaFileObject> list = new LinkedList<>();
            for(JavaFileObject file: inMemoryFileManager.list(location, path, kinds, recurse)) {
                list.add(file);
            }
            return list;
        } catch (IOException e) {
            throw new Error("Failed to list files.", e);
        }
    }
}
