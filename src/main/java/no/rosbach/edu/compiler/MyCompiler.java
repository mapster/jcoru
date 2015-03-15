package no.rosbach.edu.compiler;

import com.sun.source.util.JavacTask;

import javax.tools.*;
import java.io.IOException;
import java.util.*;

/**
 * Created by mapster on 25.11.14.
 */
public class MyCompiler {

    InMemoryFileManager fileManager;
    private final JavaCompiler compiler;

    public MyCompiler() {
        fileManager = new InMemoryFileManager(new LinkedList<JavaFileObject>());
        compiler = ToolProvider.getSystemJavaCompiler();
    }

    public Iterable<? extends JavaFileObject> compile(JavaSourceString myTestSource) throws IOException {
        return compile(Arrays.asList(myTestSource));
    }

    public Iterable<? extends JavaFileObject> compile(List<? extends JavaFileObject> files) throws IOException {
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, files);

        JavacTask javacTask = (JavacTask) task;

        Iterable<? extends JavaFileObject> generate = javacTask.generate();
        return generate;
    }

    public ClassLoader getClassLoader() {
        return fileManager.getClassLoader(StandardLocation.CLASS_PATH);
    }
}
