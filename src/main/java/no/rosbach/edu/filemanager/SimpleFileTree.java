package no.rosbach.edu.filemanager;

import static java.util.stream.Collectors.toList;

import java.util.Collection;

import javax.tools.JavaFileObject;

public class SimpleFileTree<T extends JavaFileObject> implements FileTree<T> {

  private final Collection<T> files;
  private PathSeparator separator;

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
    return files.stream().filter(f -> fileIsInPath(path, f.getName(), recurse)).collect(toList());
  }

  private boolean fileIsInPath(String path, String name, boolean recurse) {
    if (!path.endsWith(separator.toString()) && path.length() > 0) {
      path = path + separator.toString();
    }
    return name.startsWith(path) && (recurse || name.substring(path.length()).indexOf(separator.toString()) == -1);
  }
}
