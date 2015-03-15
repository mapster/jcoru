package no.rosbach.edu.compiler;

import javax.tools.JavaFileObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mapster on 15.03.15.
 */
public interface FileTree {
    String PATH_SEPARATOR = "/";

    public void add(JavaFileObject file);
    public Collection<JavaFileObject> listFiles(String path, boolean recurse);
}
