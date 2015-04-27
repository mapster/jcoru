package no.rosbach.edu.filemanager;

import static no.rosbach.edu.filemanager.FileTree.PathSeparator.FILESYSTEM;

import java.util.List;

import javax.tools.JavaFileObject;

public class SimpleFileTreeTest extends FileTreeTest {

  @Override
  FileTree getFileTree(List<JavaFileObject> files) {
    return new SimpleFileTree(FILESYSTEM, files);
  }
}
