package no.rosbach.jcoru.compile;

import no.rosbach.jcoru.filemanager.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.tools.*;
import java.io.File;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static no.rosbach.jcoru.utils.Stream.stream;

@Component
@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class JavaCompileUtil {

  @Value("${compilerLibsPath:}")
  private String comilerLibsPath;
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

    List<String> options = new LinkedList<>();

    // build classpath
    Optional<String> classPath = buildClassPath();
    classPath.ifPresent(cp -> {
      options.add("-classpath");
      options.add(cp);
    });

    JavaCompiler.CompilationTask task = javaCompiler.getTask(null, inMemoryFileManager, diagnosticListener, options, null, files);
    task.call();

    return stream(files)
        .map(
            source -> inMemoryFileManager.getJavaFileForInput(
                StandardLocation.CLASS_OUTPUT,
                inMemoryFileManager.inferBinaryName(StandardLocation.CLASS_OUTPUT, source),
                JavaFileObject.Kind.CLASS))
        .filter(Objects::nonNull)
        .map(CompiledClassObject::new)
        .collect(toList());
  }

  private Optional<String> buildClassPath() {
    Stream<File> jars = Optional.ofNullable(comilerLibsPath)
            .map(File::new)
            .filter(File::isDirectory)
            .map(File::listFiles)
            .map(Arrays::stream)
            .orElseGet(Stream::empty);

    return jars.map(File::getPath)
            .reduce((a, b) -> a + ":" + b);

  }

  public ClassLoader getClassLoader() {
    return inMemoryFileManager.getClassLoader(StandardLocation.CLASS_PATH);
  }

  public InMemoryFileManager getInMemoryFileManager() {
    return inMemoryFileManager;
  }
}
