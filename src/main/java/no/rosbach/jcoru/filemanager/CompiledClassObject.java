package no.rosbach.jcoru.filemanager;

import javax.tools.JavaFileObject;

public class CompiledClassObject extends JavaFileObjectWrapper {

  public CompiledClassObject(JavaFileObject fileObject) {
    super(fileObject);
    if (!fileObject.getKind().equals(Kind.CLASS)) {
      throw new IllegalArgumentException("Can only wrap JavaFileObjects that are Kind.CLASS.");
    }
  }

}
