package no.rosbach.jcoru.filemanager;


import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class InMemoryClassFile extends SimpleJavaFileObject {

  private ByteArrayOutputStream out = new ByteArrayOutputStream();

  /**
   * Construct an InMemoryClassFile with the given URI.
   *
   * @param name the name for this file object.
   */
  protected InMemoryClassFile(String name) {
    super(URI.create(name), Kind.CLASS);
  }

  public InMemoryClassFile(URI uri, InputStream inputStream) throws IOException {
    super(uri, Kind.CLASS);
    IOUtils.copy(inputStream, out);
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    return out;
  }

  @Override
  public InputStream openInputStream() throws IOException {
    out.flush();
    return new ByteArrayInputStream(out.toByteArray());
  }
}
