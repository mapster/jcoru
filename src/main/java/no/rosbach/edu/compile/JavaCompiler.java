package no.rosbach.edu.compile;

import com.sun.source.util.JavacTask;
import no.rosbach.edu.filemanager.InMemoryFileManager;
import no.rosbach.edu.filemanager.JavaSourceString;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mapster on 25.11.14.
 */
public class JavaCompiler {

    InMemoryFileManager fileManager;
    private final javax.tools.JavaCompiler compiler;
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

    public Iterable<? extends JavaFileObject> compile(JavaSourceString myTestSource) throws IOException {
        return compile(Arrays.asList(myTestSource));
    }

    public Iterable<? extends JavaFileObject> compile(List<? extends JavaFileObject> files) throws IOException {
        javax.tools.JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticListener, null, null, files);

        JavacTask javacTask = (JavacTask) task;

        Iterable<? extends JavaFileObject> generate = javacTask.generate();
        return generate;
    }

    public ClassLoader getClassLoader() {
        return fileManager.getClassLoader(StandardLocation.CLASS_PATH);
    }
}
