package no.rosbach.edu.compiler.filemanager;

import no.rosbach.edu.compiler.JavaSourceString;
import org.junit.Before;
import org.junit.Test;

import javax.tools.JavaFileObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by mapster on 15.03.15.
 */
public abstract class FileTreeTest {

    @Before
    public void setStage() {
    }

    @Test
    public void shouldAddRootFileToRootTree() {
        List<JavaFileObject> files = files("Test.java");
        FileTree tree = getFileTree(files);

        Collection<JavaFileObject> treeFiles = tree.listFiles("", false);

        assertEquals(1, treeFiles.size());
        assertSame(files.get(0), treeFiles.iterator().next());
    }

    @Test
    public void shouldAddFileToSubTree() {
        List<JavaFileObject> files = files("folder/Test.java");
        FileTree tree = getFileTree(files);

        Collection treeFiles = tree.listFiles("folder", false);

        assertEquals(1, treeFiles.size());
        assertSame(files.get(0), treeFiles.iterator().next());
    }

    @Test
    public void shouldNotAddFileToRoot() {
        List<JavaFileObject> files = files("folder/Test.java");
        FileTree tree = getFileTree(files);

        Collection<JavaFileObject> treeFiles = tree.listFiles("", false);
        assertEquals(0, treeFiles.size());
    }

    @Test
    public void listShouldIgnoreTrailingPathSeparatorInPath() {
        List<JavaFileObject> files = files("folder/Test.java");
        FileTree tree = getFileTree(files);

        Collection<JavaFileObject> treeFiles = tree.listFiles("folder/", false);

        assertEquals(1, treeFiles.size());
        assertSame(files.get(0), treeFiles.iterator().next());
    }

    @Test
    public void listShouldNotIncludeFilesInParentTree() {
        List<JavaFileObject> files = files("Test2.java", "folder/Test.java");
        FileTree tree = getFileTree(files);

        Collection<JavaFileObject> treeFiles = tree.listFiles("folder", false);
        assertEquals(1, treeFiles.size());
        assertNotSame(files.get(0), treeFiles.iterator().next());
    }

    @Test
    public void listShouldNotIncludeFilesInSubTree() {
        List<JavaFileObject> files = files("Test2.java", "folder/Test.java");
        FileTree tree = getFileTree(files);

        Collection<JavaFileObject> treeFiles = tree.listFiles("", false);
        assertEquals(1, treeFiles.size());
        assertNotSame(files.get(1), treeFiles.iterator().next());
    }

    @Test
    public void ableToAddFileToRoot() {
        List<JavaFileObject> files = files("folder/Test.java");
        FileTree tree = getFileTree(files);
        JavaSourceString addedFile = new JavaSourceString("Test2.java", "");
        tree.add(addedFile);

        Collection<JavaFileObject> treeFiles = tree.listFiles("", false);

        assertEquals(1, treeFiles.size());
        assertSame(addedFile, treeFiles.iterator().next());
    }

    @Test
    public void ableToAddFileToSubTree() {
        List<JavaFileObject> files = files("Test.java");
        FileTree tree = getFileTree(files);
        JavaSourceString addedFile = new JavaSourceString("folder/Test2.java", "");
        tree.add(addedFile);

        Collection<JavaFileObject> treeFiles = tree.listFiles("folder", false);

        assertEquals(1, treeFiles.size());
        assertSame(addedFile, treeFiles.iterator().next());
    }

    @Test
    public void shouldNotListFilesFromPackagesWithNamesThatStartsWithThePath() {
        List<JavaFileObject> files = files("folder/Test.java");
        FileTree tree = getFileTree(files);

        Collection<JavaFileObject> treeFiles = tree.listFiles("folde", false);

        assertEquals(0, treeFiles.size());
    }

    @Test
    public void listShouldIncludeFilesInSubTreeWhenRecurse() {
        List<JavaFileObject> files = files("Test2.java", "folder/Test.java");
        FileTree tree = getFileTree(files);

        Collection<JavaFileObject> treeFiles = tree.listFiles("", true);
        assertEquals(2, treeFiles.size());
    }

    abstract FileTree getFileTree(List<JavaFileObject> files);

    private List<JavaFileObject> files(String... names) {
        return Arrays.stream(names).map(name -> new JavaSourceString(name, "")).collect(Collectors.toList());
    }
}
