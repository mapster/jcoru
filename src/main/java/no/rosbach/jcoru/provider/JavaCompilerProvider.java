package no.rosbach.jcoru.provider;

import javax.enterprise.inject.Produces;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.ToolProvider;

public class JavaCompilerProvider {
  private JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

  @Produces
  public JavaCompiler getJavaCompiler() {
    return javaCompiler;
  }

  @Produces
  @SystemFileManager
  public JavaFileManager getSystemFileManager() {
    return javaCompiler.getStandardFileManager(null, null, null);
  }
}
