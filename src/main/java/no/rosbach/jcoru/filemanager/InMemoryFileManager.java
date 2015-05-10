package no.rosbach.jcoru.filemanager;

import static javax.tools.StandardLocation.ANNOTATION_PROCESSOR_PATH;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;
import static javax.tools.StandardLocation.PLATFORM_CLASS_PATH;
import static javax.tools.StandardLocation.SOURCE_PATH;
import static no.rosbach.jcoru.utils.Stream.stream;

import no.rosbach.jcoru.compile.TransientClassLoader;
import no.rosbach.jcoru.provider.FileManagerPackageWhitelist;
import no.rosbach.jcoru.security.WhitelistAccessManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class InMemoryFileManager implements JavaFileManager {
  private static final Logger LOGGER = LogManager.getLogger();
  private final TransientClassLoader clasOutputLoader;
  private final WhitelistAccessManager packageWhitelist;
  private final JavaFileManager systemFileManager;
  private final FileTree sources;
  private final FileTree outputClasses;
  private final FileTree classPathClasses = new SimpleFileTree<>(FileTree.PathSeparator.PACKAGE, new LinkedList<>());

  @Inject
  public InMemoryFileManager(TransientClassLoader classLoader, @FileManagerPackageWhitelist WhitelistAccessManager packageWhitelist) {
    this.clasOutputLoader = classLoader;
    this.packageWhitelist = packageWhitelist;
    this.clasOutputLoader.setFileManager(this);

    this.systemFileManager = ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, Locale.ENGLISH, Charset.defaultCharset());
    this.sources = new SimpleFileTree(FileTree.PathSeparator.FILESYSTEM);
    this.outputClasses = new SimpleFileTree(FileTree.PathSeparator.PACKAGE);
  }

  InMemoryFileManager(List<JavaFileObject> sources,
                      List<InMemoryClassFile> outputClasses,
                      JavaFileManager systemFileManager,
                      WhitelistAccessManager packageWhitelist) {
    this.packageWhitelist = packageWhitelist;
    this.clasOutputLoader = new TransientClassLoader(this);
    this.systemFileManager = systemFileManager;
    this.sources = new SimpleFileTree(FileTree.PathSeparator.FILESYSTEM, sources);
    this.outputClasses = new SimpleFileTree(FileTree.PathSeparator.PACKAGE, outputClasses);
  }

  @Override
  public ClassLoader getClassLoader(Location location) {
    return clasOutputLoader;
  }

  @Override
  public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
    if (packageWhitelist.hasAccess(packageName) || location.equals(PLATFORM_CLASS_PATH) || location.equals(ANNOTATION_PROCESSOR_PATH)) {
      Iterable<JavaFileObject> list = systemFileManager.list(location, packageName, kinds, recurse);
      return stream(list).map(file -> new ManagedFileObject(systemFileManager, file)).collect(Collectors.toList());
    }

    if (location.equals(SOURCE_PATH) && kinds.contains(JavaFileObject.Kind.SOURCE)) {
      Collection<JavaFileObject> files = sources.listFiles(packageName.replace(".", "/"), recurse);
      return files;
    }

    if (location.equals(CLASS_PATH) && kinds.contains(JavaFileObject.Kind.CLASS)) {
      Collection files = classPathClasses.listFiles(packageName, recurse);
      return files;
    }

    if (location.equals(CLASS_OUTPUT) && kinds.contains(JavaFileObject.Kind.CLASS)) {
      Collection collection = outputClasses.listFiles(packageName, recurse);
      return collection;
    }

    throw new UnsupportedLocation(
        String.format(
            "Cannot list files of kinds (%s) for package %s on location %s.",
            kinds.toString(),
            packageName,
            location));
  }

  // TODO: Should infer names of compiled sources
  @Override
  public String inferBinaryName(Location location, JavaFileObject file) {
    if (file instanceof ManagedFileObject) {
      ManagedFileObject managedFileObject = (ManagedFileObject) file;
      return managedFileObject.getFileManager().inferBinaryName(location, managedFileObject.getWrappedObject());
    }
    if (location.equals(PLATFORM_CLASS_PATH) || location.equals(ANNOTATION_PROCESSOR_PATH)) {
      return systemFileManager.inferBinaryName(location, file);
    }
    if (location.equals(CLASS_OUTPUT) && file.getKind().equals(JavaFileObject.Kind.SOURCE)) {
      return file.toUri().toString().replace(File.separatorChar, '.').replace(".java", "");
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
    return systemFileManager.handleOption(current, remaining);
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
  public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) {
    if (location.equals(CLASS_OUTPUT) && kind.equals(JavaFileObject.Kind.CLASS)) {
      return outputClasses.get(className);
    }
    if (location.equals(CLASS_PATH) && kind.equals(JavaFileObject.Kind.CLASS)) {
      return classPathClasses.get(className);
    }
    throw new UnsupportedOperationException(String.format("getJavaFileForInput is not yet supported."));
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
    if (location.equals(CLASS_OUTPUT)) {
      InMemoryClassFile inMemoryFile = new InMemoryClassFile(className);
      outputClasses.add(inMemoryFile);
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

  public void addSources(JavaFileObject... newSources) {
    for (JavaFileObject source : newSources) {
      sources.add(source);
    }
  }

  public void addClassPathClass(JavaFileObject... newClassPathClass) {
    for (JavaFileObject clazz : newClassPathClass) {
      classPathClasses.add(clazz);
    }
  }

  public void addOutputClass(JavaFileObject... newOutputClass) {
    for (JavaFileObject clazz : newOutputClass) {
      outputClasses.add(clazz);
    }
  }
}
