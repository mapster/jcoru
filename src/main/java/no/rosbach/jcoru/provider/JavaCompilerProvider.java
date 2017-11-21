package no.rosbach.jcoru.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.tools.*;

@Configuration
public class JavaCompilerProvider {
  private JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

  @Bean
  @Scope("request")
  public JavaCompiler javaCompiler() {
    return javaCompiler;
  }

  @Bean
  @Scope("request")
  public JavaFileManager systemFileManager() {
    return javaCompiler.getStandardFileManager(null, null, null);
  }
}
