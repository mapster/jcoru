package no.rosbach.jcoru.compile;


import no.rosbach.jcoru.filemanager.InMemoryFileManager;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

public class TransientClassLoader extends ClassLoader {
  private static Logger LOGGER = LogManager.getLogger();
  private InMemoryFileManager fileManager;

  public TransientClassLoader() {
    super(TransientClassLoader.class.getClassLoader());
  }

  public TransientClassLoader(InMemoryFileManager fileManager) {
    super(TransientClassLoader.class.getClassLoader());
    this.fileManager = fileManager;
  }

  public boolean isClassLoaded(String name) {
    return findLoadedClass(name) != null;
  }

  public void setFileManager(InMemoryFileManager fileManager) {
    this.fileManager = fileManager;
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    LOGGER.debug("Loading class: " + name);
    Class clazz = findClass(name);
    if (clazz == null) {
      clazz = super.loadClass(name);
    }
    return clazz;
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    Class<?> clazz = findLoadedClass(name);
    if (clazz != null) {
      return clazz;
    }

    JavaFileObject clazzFile = fileManager.getJavaFileForInput(StandardLocation.CLASS_OUTPUT, name, JavaFileObject.Kind.CLASS);
    if (clazzFile != null) {
      try {
        byte[] bytes = IOUtils.toByteArray(clazzFile.openInputStream());
        return defineClass(name, bytes, 0, bytes.length);
      } catch (IOException e) {
        throw new NonRecoverableError("Failed to read compiled class object.", e);
      }
    }

    return null;
  }
}
