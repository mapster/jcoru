package no.rosbach.jcoru.compile;

import no.rosbach.jcoru.filemanager.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.tools.*;
import java.io.File;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static no.rosbach.jcoru.utils.Stream.stream;

@Component
public class JavaCompileUtil {
  public static final String LIB_RESOURCE_DIRECTORY = "lib";

  @Resource
  private JavaCompiler javaCompiler;
  @Resource
  private InMemoryFileManager inMemoryFileManager;

  public JavaCompileUtil() {
  }

  JavaCompileUtil(JavaCompiler javaCompiler, InMemoryFileManager inMemoryFileManager) {
      this.javaCompiler = javaCompiler;
      this.inMemoryFileManager = inMemoryFileManager;
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

    JavaCompiler.CompilationTask task = javaCompiler.getTask(null, inMemoryFileManager, diagnosticListener, options, null, files);
    task.call();

    return stream(files)
        .map(
            source -> inMemoryFileManager.getJavaFileForInput(
                StandardLocation.CLASS_OUTPUT,
                inMemoryFileManager.inferBinaryName(StandardLocation.CLASS_OUTPUT, source),
                JavaFileObject.Kind.CLASS))
        .filter(compiled -> compiled != null)
        .map(CompiledClassObject::new)
        .collect(toList());
  }

  public ClassLoader getClassLoader() {
    return inMemoryFileManager.getClassLoader(StandardLocation.CLASS_PATH);
  }

  public InMemoryFileManager getInMemoryFileManager() {
    return inMemoryFileManager;
  }
}
