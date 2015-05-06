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

import javax.inject.Inject;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

public class JavaCompileUtil {
  public static final String LIB_RESOURCE_DIRECTORY = "lib";

  private final JavaCompiler compiler;
  InMemoryFileManager fileManager;

  @Inject
  public JavaCompileUtil(JavaCompiler compiler) {
    fileManager = new InMemoryFileManager(new LinkedList<>());
    this.compiler = compiler;
  }

  public List<CompiledClassObject> compile(JavaSourceString myTestSource, DiagnosticListener diagnosticListener) {
    return compile(Arrays.asList(myTestSource), diagnosticListener);
  }

  public List<CompiledClassObject> compile(List<JavaSourceString> files, DiagnosticListener diagnosticListener) {
    if (files.isEmpty()) {
      return new LinkedList<>();
    }

    List<String> options = new LinkedList<>();
    options.add("-classpath");
    options.add(JavaCompileUtil.class.getClassLoader().getResource(LIB_RESOURCE_DIRECTORY + File.separatorChar + "junit-4.11.jar").getFile());

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
