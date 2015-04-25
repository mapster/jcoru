package no.rosbach.edu.filemanager;

import javax.tools.JavaFileObject;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by mapster on 15.03.15.
 */
public class SimpleFileTree<T extends JavaFileObject> implements FileTree<T> {

    private PathSeparator separator;
    private final Collection<T> files;

    public SimpleFileTree(PathSeparator separator, Collection<T> files) {
        this.separator = separator;
        this.files = files;
    }

    @Override
    public void add(T file) {
        files.add(file);
    }

    @Override
    public Collection<T> listFiles(String path, boolean recurse) {
        return files.stream().filter(f -> fileIsInPath(path, f.getName(), recurse)).collect(Collectors.toList());
    }

    private boolean fileIsInPath(String path, String name, boolean recurse) {
        if(!path.endsWith(separator.toString()) && path.length() > 0) {
            path = path + separator.toString();
        }
        return name.startsWith(path) && (recurse || name.substring(path.length()).indexOf(separator.toString()) == -1);
    }
}
