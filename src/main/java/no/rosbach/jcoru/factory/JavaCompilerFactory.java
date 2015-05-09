package no.rosbach.jcoru.factory;

import org.glassfish.hk2.api.Factory;

import javax.enterprise.inject.Produces;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaCompilerFactory implements Factory<JavaCompiler> {

  @Produces
  public JavaCompiler provide() {
    return ToolProvider.getSystemJavaCompiler();
  }

  @Override
  public void dispose(JavaCompiler compiler) {

  }
}
