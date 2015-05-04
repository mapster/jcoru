package no.rosbach.jcoru.compile;

import static java.util.stream.Collectors.toList;
import static no.rosbach.jcoru.utils.Stream.stream;

import no.rosbach.jcoru.filemanager.CompiledClassObject;
import no.rosbach.jcoru.filemanager.InMemoryFileManager;
import no.rosbach.jcoru.filemanager.JavaSourceString;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class JavaCompiler {
  public static final String LIB_RESOURCE_DIRECTORY = "lib";

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

  JavaCompiler(javax.tools.JavaCompiler compiler, DiagnosticListener diagnosticListener) {
    fileManager = new InMemoryFileManager(new LinkedList<>());
    this.compiler = compiler;
    this.diagnosticListener = diagnosticListener;
  }

  public List<CompiledClassObject> compile(JavaSourceString myTestSource) {
    return compile(Arrays.asList(myTestSource));
  }

  public List<CompiledClassObject> compile(List<JavaSourceString> files) {
    if (files.isEmpty()) {
      return new LinkedList<>();
    }

    List<String> options = new LinkedList<>();
    options.add("-classpath");
    options.add(JavaCompiler.class.getClassLoader().getResource(LIB_RESOURCE_DIRECTORY + File.separatorChar + "junit-4.11.jar").getFile());

    javax.tools.JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticListener, options, null, files);

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
