package no.rosbach.jcoru.compile;


import no.rosbach.jcoru.filemanager.InMemoryFileManager;
import no.rosbach.jcoru.security.AccessManager;
import no.rosbach.jcoru.security.StrictAccessControlException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;

@Component
@Scope("request")
public class TransientClassLoader extends ClassLoader {
  private static Logger LOGGER = LoggerFactory.getLogger(TransientClassLoader.class);
  @Resource
  private AccessManager<String> classLoaderWhitelist;
  private InMemoryFileManager inMemoryFileManager;

  public TransientClassLoader() {
    super(TransientClassLoader.class.getClassLoader());
  }

  public TransientClassLoader(AccessManager<String> classLoaderWhitelist) {
    super(TransientClassLoader.class.getClassLoader());
    this.classLoaderWhitelist = classLoaderWhitelist;
  }

  public boolean isClassLoaded(String name) {
    return findLoadedClass(name) != null;
  }

  public void setInMemoryFileManager(InMemoryFileManager inMemoryFileManager) {
    this.inMemoryFileManager = inMemoryFileManager;
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    LOGGER.debug("Requesting to load class: {}", name);
    if (classLoaderWhitelist.hasAccess(name) || isKnownSource(name)) {
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

    JavaFileObject clazzFile = inMemoryFileManager.getJavaFileForInput(StandardLocation.CLASS_OUTPUT, name, JavaFileObject.Kind.CLASS);
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
    return inMemoryFileManager.getJavaFileForInput(StandardLocation.SOURCE_PATH, name, JavaFileObject.Kind.SOURCE) != null ||
        inMemoryFileManager.getJavaFileForInput(StandardLocation.CLASS_OUTPUT, name, JavaFileObject.Kind.CLASS) != null;
  }

}
