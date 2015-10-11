package no.rosbach.jcoru.provider;

import no.rosbach.jcoru.compile.JavaCompileUtil;
import no.rosbach.jcoru.compile.TransientClassLoader;
import no.rosbach.jcoru.filemanager.InMemoryFileManager;

import javax.enterprise.inject.Produces;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.ToolProvider;

public class JavaCompilerProvider {
  private static final WhitelistProvider whitelistProvider = new WhitelistProvider();
  private JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

  @Produces
  public JavaCompileUtil getJavaCompileUtil() {
    return new JavaCompileUtil(getJavaCompiler(), getInMemoryFileManager());
  }

  @Produces
  public InMemoryFileManager getInMemoryFileManager() {
    return new InMemoryFileManager(getSystemFileManager(), getTransientClassLoader(), whitelistProvider.getFileManagerPackagesWhitelist());
  }

  public TransientClassLoader getTransientClassLoader() {
    return new TransientClassLoader(whitelistProvider.getClassloaderWhitelist());
  }

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
