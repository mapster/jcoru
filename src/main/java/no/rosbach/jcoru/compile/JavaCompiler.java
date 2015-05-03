package no.rosbach.jcoru.compile;

import static java.util.stream.Collectors.toList;
import static no.rosbach.jcoru.utils.Stream.stream;

import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.filemanager.InMemoryFileManager;
import no.rosbach.jcoru.filemanager.JavaSourceString;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

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

  public List<CompiledClassObject> compile(JavaSourceString myTestSource) {
    return compile(Arrays.asList(myTestSource));
  }

  public List<CompiledClassObject> compile(List<JavaSourceString> files) {
    javax.tools.JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticListener, null, null, files);

    task.call();
    return stream(files)
        .map(
            source -> fileManager.getJavaFileForInput(
                StandardLocation.CLASS_OUTPUT,
                fileManager.inferBinaryName(StandardLocation.CLASS_OUTPUT, source),
                JavaFileObject.Kind.CLASS))
        .filter(compiled -> compiled != null)
        .map(CompiledClassObject::new)
        .collect(toList());
  }

  public ClassLoader getClassLoader() {
    return fileManager.getClassLoader(StandardLocation.CLASS_PATH);
  }
}
