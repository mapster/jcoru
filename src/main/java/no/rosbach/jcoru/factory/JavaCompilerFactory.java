package no.rosbach.jcoru.factory;

import javax.enterprise.inject.Produces;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaCompilerFactory {

  @Produces
  public JavaCompiler provide() {
    return ToolProvider.getSystemJavaCompiler();
  }

}
