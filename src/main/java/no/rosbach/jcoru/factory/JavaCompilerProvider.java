package no.rosbach.jcoru.factory;

import no.rosbach.jcoru.compile.NonRecoverableError;
import no.rosbach.jcoru.security.WhitelistAccessManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.inject.Produces;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaCompilerProvider {
  private JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

  @Produces
  public JavaCompiler getJavaCompiler() {
    return javaCompiler;
  }

  @Produces
  @FileManagerPackageWhitelist
  public WhitelistAccessManager getFileManagerPackagesWhitelist() {
    return getWhitelistFromFile("filemanager-packages.json");
  }

  private WhitelistAccessManager getWhitelistFromFile(String name) {
    String resource = "whitelists/" + name;
    try (InputStream fixtureStream = getClass().getClassLoader().getResourceAsStream(resource)) {
      ObjectMapper mapper = new ObjectMapper();
      return WhitelistAccessManager.fromJson(mapper.readValue(fixtureStream, ArrayNode.class));
    } catch (IOException e) {
      throw new NonRecoverableError("Failed to read whitelist: " + resource, e);
    }
  }
}
