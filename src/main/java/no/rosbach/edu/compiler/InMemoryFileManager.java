package no.rosbach.edu.compiler;

import javax.tools.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import static javax.tools.StandardLocation.*;

/**
* Created by mapster on 30.11.14.
*/
class InMemoryFileManager implements JavaFileManager {
    private final ClassLoader classPathLoader;
    private final JavaFileManager systemFileManager;
    private final FileTree sources;
    Map<String, InMemoryClassFile> classStore;

    public InMemoryFileManager(List<JavaFileObject> sources){
        this.classStore = new HashMap<>();
        classPathLoader = new TransientClassLoader(this.classStore);
        this.systemFileManager = ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, Locale.ENGLISH, Charset.defaultCharset());
        this.sources = new SimpleFileTree(sources);
    }

    InMemoryFileManager(List<JavaFileObject> sources, Map<String, InMemoryClassFile> classStore, JavaFileManager systemFileManager){
        this.classStore = classStore;
        classPathLoader = new TransientClassLoader(classStore);
        this.systemFileManager = systemFileManager;
        this.sources = new SimpleFileTree(sources);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return classPathLoader;
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        if(location.equals(PLATFORM_CLASS_PATH) || location.equals(ANNOTATION_PROCESSOR_PATH)){
            return systemFileManager.list(location, packageName, kinds, recurse);
        }
        else if(location.equals(SOURCE_PATH) && kinds.contains(JavaFileObject.Kind.SOURCE)){
            Collection<JavaFileObject> files = sources.listFiles(packageName.replace(".", "/"), recurse);
            return files;
        }
        else if (location.equals(CLASS_PATH)) {
            return new LinkedList<>();
        }
        throw new Error(String.format("unsupported arguments: list(%s, %s, %s, %s)", location, packageName, kinds.toString(), Boolean.toString(recurse)));
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if(location.equals(PLATFORM_CLASS_PATH) || location.equals(ANNOTATION_PROCESSOR_PATH)){
            return systemFileManager.inferBinaryName(location, file);
        }
        else if(location.equals(SOURCE_PATH)) {
            return file.toUri().toString();
        }
        throw new Error("inferBinaryName not implemented yet.");
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        throw new Error("isSameFile not implemented yet.");
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        throw new Error("handleOption not implemented yet.");
    }

    @Override
    public boolean hasLocation(Location location) {
        if(location instanceof StandardLocation) {
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
        throw new Error("hasLocation not implemented yet.");
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        throw new Error("getJavaFileForInput not implemented yet.");
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        if(location.equals(CLASS_OUTPUT)){
            InMemoryClassFile javaClass = new InMemoryClassFile(className);
            classStore.put(javaClass.getName(), javaClass);
            return javaClass;
        }
        throw new Error("getJavaFileForOutput not implemented yet.");
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        throw new Error("getFileForInput not implemented yet.");
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        throw new Error("getFileForOutput not implemented yet.");
    }

    @Override
    public void flush() throws IOException {
//            throw new Error("flush not implemented yet.");
    }

    @Override
    public void close() throws IOException {
        throw new Error("close not implemented yet.");
    }

    @Override
    public int isSupportedOption(String option) {
        throw new Error("isSupportedOption not implemented yet.");
    }
}
