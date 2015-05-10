package no.rosbach.jcoru.factory;

import javax.enterprise.inject.Produces;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaCompilerProvider {
  private JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

  @Produces
  public JavaCompiler getJavaCompiler() {
    return javaCompiler;
  }

}
