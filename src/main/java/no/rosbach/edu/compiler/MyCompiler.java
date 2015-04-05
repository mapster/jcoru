package no.rosbach.edu.compiler;

import com.sun.source.util.JavacTask;
import no.rosbach.edu.compiler.filemanager.InMemoryFileManager;

import javax.tools.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mapster on 25.11.14.
 */
class MyCompiler {

    InMemoryFileManager fileManager;
    private final JavaCompiler compiler;
    private DiagnosticListener diagnosticListener;

    public MyCompiler() {
        fileManager = new InMemoryFileManager(new LinkedList<>());
        compiler = ToolProvider.getSystemJavaCompiler();
    }

    public MyCompiler(DiagnosticListener diagnosticListener) {
        fileManager = new InMemoryFileManager(new LinkedList<>());
        this.compiler = ToolProvider.getSystemJavaCompiler();
        this.diagnosticListener = diagnosticListener;
    }

    public Iterable<? extends JavaFileObject> compile(JavaSourceString myTestSource) throws IOException {
        return compile(Arrays.asList(myTestSource));
    }

    public Iterable<? extends JavaFileObject> compile(List<? extends JavaFileObject> files) throws IOException {
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticListener, null, null, files);

        JavacTask javacTask = (JavacTask) task;

        Iterable<? extends JavaFileObject> generate = javacTask.generate();
        return generate;
    }

    public ClassLoader getClassLoader() {
        return fileManager.getClassLoader(StandardLocation.CLASS_PATH);
    }
}
