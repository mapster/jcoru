package no.rosbach.jcoru.compile;


import no.rosbach.jcoru.filemanager.InMemoryFileManager;
import no.rosbach.jcoru.security.AccessManager;
import no.rosbach.jcoru.security.StrictAccessControlException;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

public class TransientClassLoader extends ClassLoader {
  private static Logger LOGGER = LogManager.getLogger();
  private final AccessManager<String> classesWhitelist;
  private InMemoryFileManager fileManager;

  public TransientClassLoader(AccessManager<String> classesWhitelist) {
    super(TransientClassLoader.class.getClassLoader());
    this.classesWhitelist = classesWhitelist;
  }

  public boolean isClassLoaded(String name) {
    return findLoadedClass(name) != null;
  }

  public void setFileManager(InMemoryFileManager fileManager) {
    this.fileManager = fileManager;
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    LOGGER.debug("Requesting to load class: {}", name);
    if (classesWhitelist.hasAccess(name) || isKnownSource(name)) {
      LOGGER.debug("Permitted to load class: {}", name);
      Class clazz = findClass(name);
      if (clazz == null) {
        LOGGER.debug("Loading class with parent: {}", name);
        clazz = super.loadClass(name);
      }
      return clazz;
    } else {
      LOGGER.error("Attempted to load non-whitlisted class: {}.", name);
      throw new StrictAccessControlException("Attempted to load non-whitlisted class: " + name);
    }
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    Class<?> clazz = findLoadedClass(name);
    if (clazz != null) {
      LOGGER.debug("Class already loaded: {}", name);
      return clazz;
    }

    JavaFileObject clazzFile = fileManager.getJavaFileForInput(StandardLocation.CLASS_OUTPUT, name, JavaFileObject.Kind.CLASS);
    if (clazzFile != null) {
      try {
        byte[] bytes = IOUtils.toByteArray(clazzFile.openInputStream());
        LOGGER.info("Loading class: {}", name);
        return defineClass(name, bytes, 0, bytes.length);
      } catch (IOException e) {
        throw new NonRecoverableError("Failed to read compiled class object.", e);
      }
    }

    return null;
  }

  private boolean isKnownSource(String name) {
    return fileManager.getJavaFileForInput(StandardLocation.SOURCE_PATH, name, JavaFileObject.Kind.SOURCE) != null ||
        fileManager.getJavaFileForInput(StandardLocation.CLASS_OUTPUT, name, JavaFileObject.Kind.CLASS) != null;
  }
}
