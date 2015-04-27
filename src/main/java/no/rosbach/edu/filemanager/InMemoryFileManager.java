package no.rosbach.edu.filemanager;

import static javax.tools.StandardLocation.ANNOTATION_PROCESSOR_PATH;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;
import static javax.tools.StandardLocation.PLATFORM_CLASS_PATH;
import static javax.tools.StandardLocation.SOURCE_PATH;
import static no.rosbach.edu.utils.Stream.stream;

import no.rosbach.edu.compile.TransientClassLoader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class InMemoryFileManager implements JavaFileManager {
  private static final String PROPERTIES_PATH = "filemanager.properties";
  private static final String PROPERTIES_LIST_DELIMITER = ",";
  private static final String DELELEGATE_TO_PARENT = "delegate_to_parent.packages";
  private static Set<String> delegate_packages;
  private static Properties properties = loadProperties();
  private final ClassLoader classPathLoader;
  private final JavaFileManager systemFileManager;
  private final FileTree sources;
  private final FileTree compiledClasses;
  Map<String, InMemoryClassFile> classStore;

  public InMemoryFileManager(List<JavaFileObject> sources) {
    this.classStore = new HashMap<>();
    classPathLoader = new TransientClassLoader(this.classStore);
    this.systemFileManager = ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, Locale.ENGLISH, Charset.defaultCharset());
    this.sources = new SimpleFileTree(FileTree.PathSeparator.FILESYSTEM, sources);
    this.compiledClasses = new SimpleFileTree(FileTree.PathSeparator.PACKAGE, classStore.values());
  }

  InMemoryFileManager(List<JavaFileObject> sources, Map<String, InMemoryClassFile> classStore, JavaFileManager systemFileManager) {
    this.classStore = classStore;
    classPathLoader = new TransientClassLoader(classStore);
    this.systemFileManager = systemFileManager;
    this.sources = new SimpleFileTree(FileTree.PathSeparator.FILESYSTEM, sources);
    this.compiledClasses = new SimpleFileTree(FileTree.PathSeparator.PACKAGE, classStore.values());
  }

  private static Properties loadProperties() {
    Properties properties = new Properties();
    try {
      properties.load(InMemoryFileManager.class.getClassLoader().getResourceAsStream(PROPERTIES_PATH));
    } catch (IOException e) {
      //TODO: log entry
      return new Properties();
    }

    delegate_packages = new HashSet<String>(Arrays.asList(properties.getProperty(DELELEGATE_TO_PARENT).split(PROPERTIES_LIST_DELIMITER)));

    return properties;
  }

  @Override
  public ClassLoader getClassLoader(Location location) {
    return classPathLoader;
  }

  @Override
  public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
    if (delegate_packages.contains(packageName) || location.equals(PLATFORM_CLASS_PATH) || location.equals(ANNOTATION_PROCESSOR_PATH)) {
      Iterable<JavaFileObject> list = systemFileManager.list(location, packageName, kinds, recurse);
      return stream(list).map(file -> new ManagedFileObject(systemFileManager, file)).collect(Collectors.toList());
    } else if (location.equals(SOURCE_PATH) && kinds.contains(JavaFileObject.Kind.SOURCE)) {
      Collection<JavaFileObject> files = sources.listFiles(packageName.replace(".", "/"), recurse);
      return files;
    } else if (location.equals(CLASS_PATH) && kinds.contains(JavaFileObject.Kind.CLASS)) {
      return compiledClasses.listFiles(packageName, false);
    }
    throw new UnsupportedLocation(
        String.format(
            "Cannot list files of kinds (%s) for package %s on location %s.",
            kinds.toString(),
            packageName,
            location));
  }

  @Override
  public String inferBinaryName(Location location, JavaFileObject file) {
    if (file instanceof ManagedFileObject) {
      ManagedFileObject managedFileObject = (ManagedFileObject) file;
      return managedFileObject.getFileManager().inferBinaryName(location, managedFileObject.getFileObject());
    }
    if (location.equals(PLATFORM_CLASS_PATH) || location.equals(ANNOTATION_PROCESSOR_PATH)) {
      return systemFileManager.inferBinaryName(location, file);
    }
    if (location.equals(SOURCE_PATH) || location.equals(CLASS_PATH)) {
      return file.toUri().toString();
    }
    throw new UnsupportedLocation(String.format("Cannot infer binary name of %s for location %s.", file.toUri().toString(), location.getName()));
  }

  @Override
  public boolean isSameFile(FileObject file1, FileObject file2) {
    throw new UnsupportedOperationException("isSameFile not implemented yet.");
  }

  @Override
  public boolean handleOption(String current, Iterator<String> remaining) {
    throw new UnsupportedOperationException("handleOption not implemented yet.");
  }

  @Override
  public boolean hasLocation(Location location) {
    if (location instanceof StandardLocation) {
      StandardLocation stdLocation = (StandardLocation) location;
      switch (stdLocation) {
        case CLASS_OUTPUT:
        case CLASS_PATH:
        case SOURCE_PATH:
        case SOURCE_OUTPUT:
          return true;
        case ANNOTATION_PROCESSOR_PATH:
        case PLATFORM_CLASS_PATH:
          return systemFileManager.hasLocation(location);
        default:
          return false;
      }
    }
    throw new UnsupportedLocation("Only support StandardLocations.");
  }

  @Override
  public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
    throw new UnsupportedOperationException(String.format("getJavaFileForInput is not yet supported."));
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
    if (location.equals(CLASS_OUTPUT)) {
      InMemoryClassFile inMemoryFile = new InMemoryClassFile(className);
      classStore.put(inMemoryFile.getName(), inMemoryFile);
      return new ManagedFileObject(this, inMemoryFile);
    }
    throw new UnsupportedLocation(String.format("Cannot get java file for output for location %s.", location.getName()));
  }

  @Override
  public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
    throw new UnsupportedOperationException("getFileForInput not implemented yet.");
  }

  @Override
  public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
    throw new UnsupportedOperationException("getFileForOutput not implemented yet.");
  }

  @Override
  public void flush() throws IOException {
//            throw new Error("flush not implemented yet.");
  }

  @Override
  public void close() throws IOException {
    throw new UnsupportedOperationException("close not implemented yet.");
  }

  @Override
  public int isSupportedOption(String option) {
    throw new UnsupportedOperationException("isSupportedOption not implemented yet.");
  }
}
