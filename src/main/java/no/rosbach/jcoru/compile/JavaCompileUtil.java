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
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

public class JavaCompileUtil {
  public static final String LIB_RESOURCE_DIRECTORY = "lib";

  private final JavaCompiler compiler;
  private final InMemoryFileManager fileManager;

  public JavaCompileUtil(JavaCompiler compiler, InMemoryFileManager fileManager) {
    this.fileManager = fileManager;
    this.compiler = compiler;
  }

  public List<CompiledClassObject> compile(JavaSourceString myTestSource, DiagnosticListener diagnosticListener) {
    return compile(Arrays.asList(myTestSource), diagnosticListener);
  }

  public List<CompiledClassObject> compile(List<JavaSourceString> files, DiagnosticListener diagnosticListener) {
    if (files.isEmpty()) {
      return new LinkedList<>();
    }

    // build classpath
    List<String> options = new LinkedList<>();
    File[] libs = new File(this.getClass().getClassLoader().getResource(JavaCompileUtil.LIB_RESOURCE_DIRECTORY).getFile()).listFiles();
    if (libs.length > 0) {
      options.add("-classpath");
      options.add(stream(libs).map(lib -> lib.getPath()).reduce("", (l1, l2) -> l1 + (l1.isEmpty() ? "" : ":") + l2));
    }

    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticListener, options, null, files);
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

  public InMemoryFileManager getFileManager() {
    return fileManager;
  }
}
