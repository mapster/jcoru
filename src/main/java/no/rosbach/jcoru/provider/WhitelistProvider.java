package no.rosbach.jcoru.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import no.rosbach.jcoru.compile.NonRecoverableError;
import no.rosbach.jcoru.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class WhitelistProvider {
  public static final HashSet<Permission> REQUIRED_SECURITY_PERMISSIONS = new HashSet<>(
      Arrays.asList(
          new RuntimePermission("accessClassInPackage.org.apache.logging.log4j")
          , new RuntimePermission("accessClassInPackage.org.apache.logging.log4j.message")
          , new RuntimePermission("createClassLoader") // Was not able to
          , new ReflectPermission("suppressAccessChecks")
      ));
  private final WhitelistAccessManager classloader = WhitelistAccessManager.fromJson(getWhitelistJsonFromFile("classes.json"));
  private final WhitelistAccessManager fileManagerPackages = WhitelistAccessManager.fromJson(getWhitelistJsonFromFile("filemanager-packages.json"));
  private final PermissionWhitelist permissionsWhitelist = PermissionWhitelist.fromJson(getWhitelistJsonFromFile("security-access.json"));

  @Bean
  public AccessManager<String> classLoaderWhitelist() {
    return classloader;
  }

  @Bean
  public AccessManager<String> fileManagerPackageWhitelist() {
    return fileManagerPackages;
  }

  @Bean
  public AccessManager<Permission> securityManagerWhitelist() {
    return permissionsWhitelist.extend(REQUIRED_SECURITY_PERMISSIONS);
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
