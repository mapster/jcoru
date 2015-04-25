package no.rosbach.edu.filemanager;

import javax.tools.JavaFileObject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mapster on 15.03.15.
 */
public class SimpleFileTree implements FileTree {

    private final List<JavaFileObject> files;

    public SimpleFileTree(Collection<JavaFileObject> files) {
        this.files = new LinkedList<>(files);
    }

    @Override
    public void add(JavaFileObject file) {
        files.add(file);
    }

    @Override
    public Collection<JavaFileObject> listFiles(String path, boolean recurse) {
        return files.stream().filter(f -> fileIsInPath(path, f.getName(), recurse)).collect(Collectors.toList());
    }

    private boolean fileIsInPath(String path, String name, boolean recurse) {
        if(!path.endsWith(PATH_SEPARATOR) && path.length() > 0) {
            path = path + PATH_SEPARATOR;
        }
        return name.startsWith(path) && (recurse || name.substring(path.length()).indexOf(PATH_SEPARATOR) == -1);
    }
}
