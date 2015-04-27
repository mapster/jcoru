package no.rosbach.edu.compile;

import com.sun.source.util.JavacTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import no.rosbach.edu.filemanager.InMemoryFileManager;

/**
 * Created by mapster on 25.11.14.
 */
public class JavaCompiler {

    private final javax.tools.JavaCompiler compiler;
    InMemoryFileManager fileManager;
    private DiagnosticListener diagnosticListener;

    public JavaCompiler() {
        fileManager = new InMemoryFileManager(new LinkedList<>());
        compiler = ToolProvider.getSystemJavaCompiler();
    }

    public JavaCompiler(DiagnosticListener diagnosticListener) {
        fileManager = new InMemoryFileManager(new LinkedList<>());
        this.compiler = ToolProvider.getSystemJavaCompiler();
        this.diagnosticListener = diagnosticListener;
    }

    public Iterable<? extends JavaFileObject> compile(JavaFileObject myTestSource) {
        return compile(Arrays.asList(myTestSource));
    }

    public Iterable<? extends JavaFileObject> compile(List<? extends JavaFileObject> files) {
        javax.tools.JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticListener, null, null, files);

        JavacTask javacTask = (JavacTask) task;

        try {
            return javacTask.generate();
        } catch (IOException e) {
            throw new NonRecoverableError("IOException occured while compiling sources.", e);
        }
    }

    public ClassLoader getClassLoader() {
        return fileManager.getClassLoader(StandardLocation.CLASS_PATH);
    }
}
