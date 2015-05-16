package no.rosbach.jcoru.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class PreloadClassesServlet implements ServletContextListener {
  public static final String PRELOAD_CLASSES_FILE = "preload-classes.txt";
  private static final Logger LOGGER = LogManager.getLogger();

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    LOGGER.info("Pre-loading classes.");
    try (InputStream classesStream = getClass().getClassLoader().getResourceAsStream(PRELOAD_CLASSES_FILE)) {
      Scanner sc = new Scanner(classesStream);
      while (sc.hasNextLine()) {
        try {
          Class.forName(sc.nextLine().trim());
        } catch (ClassNotFoundException e) {
          LOGGER.error("Failed to pre-load class.", e);
        }
      }

    } catch (IOException e) {
      LOGGER.error("Failed to pre-load classes.", e);
    }
    LOGGER.info("Successfully pre-loaded all classes.");
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {

  }
}
