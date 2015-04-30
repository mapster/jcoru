package no.rosbach.jcoru.filemanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

public abstract class JavaFileObjectWrapper implements JavaFileObject {
  protected final JavaFileObject fileObject;

  public JavaFileObjectWrapper(JavaFileObject fileObject) {
    this.fileObject = fileObject;
  }

  public static JavaFileObject getWrappedObject(JavaFileObject object) {
    if (object instanceof JavaFileObjectWrapper) {
      return ((JavaFileObjectWrapper) object).fileObject;
    }
    return object;
  }

  public JavaFileObject getFileObject() {
    return fileObject;
  }

  @Override
  public Kind getKind() {
    return fileObject.getKind();
  }

  @Override
  public boolean isNameCompatible(String simpleName, Kind kind) {
    return fileObject.isNameCompatible(simpleName, kind);
  }

  @Override
  public NestingKind getNestingKind() {
    return fileObject.getNestingKind();
  }

  @Override
  public Modifier getAccessLevel() {
    return fileObject.getAccessLevel();
  }

  @Override
  public URI toUri() {
    return fileObject.toUri();
  }

  @Override
  public String getName() {
    return fileObject.getName();
  }

  @Override
  public InputStream openInputStream() throws IOException {
    return fileObject.openInputStream();
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    return fileObject.openOutputStream();
  }

  @Override
  public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
    return fileObject.openReader(ignoreEncodingErrors);
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
    return fileObject.getCharContent(ignoreEncodingErrors);
  }

  @Override
  public Writer openWriter() throws IOException {
    return fileObject.openWriter();
  }

  @Override
  public long getLastModified() {
    return fileObject.getLastModified();
  }

  @Override
  public boolean delete() {
    return fileObject.delete();
  }
}
