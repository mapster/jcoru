package no.rosbach.jcoru.compile;

import no.rosbach.jcoru.filemanager.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.tools.*;
import java.io.File;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static no.rosbach.jcoru.utils.Stream.stream;

@Component
@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class JavaCompileUtil {
  public static final String LIB_RESOURCE_DIRECTORY = "lib";

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

    // build classpath
    List<String> options = new LinkedList<>();
    File[] libs = getLibDirectory().listFiles();
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
        .filter(Objects::nonNull)
        .map(CompiledClassObject::new)
        .collect(toList());
  }

  private File getLibDirectory() {
    if (StringUtils.isEmpty(comilerLibsPath)) {
      return new File(this.getClass().getClassLoader().getResource(JavaCompileUtil.LIB_RESOURCE_DIRECTORY).getFile());
    }
    return new File(comilerLibsPath);
  }

  public ClassLoader getClassLoader() {
    return inMemoryFileManager.getClassLoader(StandardLocation.CLASS_PATH);
  }

  public InMemoryFileManager getInMemoryFileManager() {
    return inMemoryFileManager;
  }
}
