package no.rosbach.jcoru.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.io.InputStream;

public class WhitelistTestBase {
  protected ArrayNode getWhitelistJson(final String resource) {
    try (InputStream fixtureStream = getClass().getClassLoader().getResourceAsStream(resource)) {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(fixtureStream, ArrayNode.class);
    } catch (IOException e) {
      throw new Error("Failed to read whitelist fixture: " + resource, e);
    }
  }
}
