package no.rosbach.jcoru.provider;

import no.rosbach.jcoru.compile.NonRecoverableError;
import no.rosbach.jcoru.security.AccessManager;
import no.rosbach.jcoru.security.PermissionWhitelist;
import no.rosbach.jcoru.security.WhitelistAccessManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.io.InputStream;
import java.security.Permission;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class WhitelistProvider {
  private final WhitelistAccessManager classloader = WhitelistAccessManager.fromJson(getWhitelistJsonFromFile("classes.json"));
  private final WhitelistAccessManager fileManagerPackages = WhitelistAccessManager.fromJson(getWhitelistJsonFromFile("filemanager-packages.json"));
  private final PermissionWhitelist permissionsWhitelist = PermissionWhitelist.fromJson(getWhitelistJsonFromFile("security-access.json"));

  @Produces
  @ClassloaderWhitelist
  public AccessManager<String> getClassloaderWhitelist() {
    return classloader;
  }

  @Produces
  @FileManagerPackageWhitelist
  public AccessManager<String> getFileManagerPackagesWhitelist() {
    return fileManagerPackages;
  }

  @Produces
  @SecurityManagerWhitelist
  public AccessManager<Permission> getSecurityManagerWhitelist() {
    return permissionsWhitelist;
  }

  private ArrayNode getWhitelistJsonFromFile(String name) {
    String resource = "whitelists/" + name;
    try (InputStream fixtureStream = getClass().getClassLoader().getResourceAsStream(resource)) {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(fixtureStream, ArrayNode.class);
    } catch (IOException e) {
      throw new NonRecoverableError("Failed to read whitelist: " + resource, e);
    }
  }
}
