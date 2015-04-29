package no.rosbach.jcoru.compile;


import no.rosbach.jcoru.filemanager.InMemoryClassFile;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Map;

public class TransientClassLoader extends ClassLoader {

  private Map<String, InMemoryClassFile> classStore;

  public TransientClassLoader(Map<String, InMemoryClassFile> classStore) {
    super(TransientClassLoader.class.getClassLoader());
    this.classStore = classStore;
  }

  public boolean isClassLoaded(String name) {
    return findLoadedClass(name) != null;
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    if (classStore.containsKey(name)) {
      return findClass(name);
    }
    return super.loadClass(name);
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    Class<?> clazz = findLoadedClass(name);
    if (clazz != null) {
      return clazz;
    }

    try {
      InMemoryClassFile javaClass = classStore.get(name);
      byte[] bytes = IOUtils.toByteArray(javaClass.openInputStream());
      return defineClass(name, bytes, 0, bytes.length);
    } catch (IOException e) {
      throw new Error(e);
    }
  }
}
