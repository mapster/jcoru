package no.rosbach.jcoru.filemanager;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public class ManagedFileObject extends JavaFileObjectWrapper {
  private final JavaFileManager fileManager;

  public ManagedFileObject(JavaFileManager fileManager, JavaFileObject fileObject) {
    super(fileObject);
    this.fileManager = fileManager;
  }

  public JavaFileManager getFileManager() {
    return fileManager;
  }

}
