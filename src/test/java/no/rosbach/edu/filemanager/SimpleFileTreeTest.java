package no.rosbach.edu.filemanager;

import javax.tools.JavaFileObject;
import java.util.List;

/**
 * Created by mapster on 15.03.15.
 */
public class SimpleFileTreeTest extends FileTreeTest {

    @Override
    FileTree getFileTree(List<JavaFileObject> files) {
        return new SimpleFileTree(FileTree.PathSeparator.FILESYSTEM, files);
    }
}
