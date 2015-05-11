package no.rosbach.jcoru.provider;

import no.rosbach.jcoru.compile.NonRecoverableError;
import no.rosbach.jcoru.security.WhitelistAccessManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class WhitelistProvider {
  private final WhitelistAccessManager classloader = getWhitelistFromFile("classes.json");
  private final WhitelistAccessManager fileManagerPackages = getWhitelistFromFile("filemanager-packages.json");
  private final WhitelistAccessManager securityManager = getWhitelistFromFile("security-access.json");

  @Produces
  @ClassloaderWhitelist
  public WhitelistAccessManager getClassloaderWhitelist() {
    return classloader;
  }

  @Produces
  @FileManagerPackageWhitelist
  public WhitelistAccessManager getFileManagerPackagesWhitelist() {
    return fileManagerPackages;
  }

  @Produces
  @SecurityManagerWhitelist
  public WhitelistAccessManager getSecurityManagerWhitelist() {
    return securityManager;
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
