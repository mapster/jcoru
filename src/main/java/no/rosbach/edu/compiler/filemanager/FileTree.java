package no.rosbach.edu.compiler.filemanager;

import javax.tools.JavaFileObject;
import java.util.Collection;

/**
 * Created by mapster on 15.03.15.
 */
public interface FileTree {
    String PATH_SEPARATOR = "/";

    public void add(JavaFileObject file);
    public Collection<JavaFileObject> listFiles(String path, boolean recurse);
}
