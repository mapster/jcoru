package no.rosbach.jcoru.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.tools.*;

@Configuration
public class JavaCompilerProvider {
  private JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

  @Bean
  public JavaCompiler javaCompiler() {
    return javaCompiler;
  }

  @Bean
  public JavaFileManager systemFileManager() {
    return javaCompiler.getStandardFileManager(null, null, null);
  }
}
