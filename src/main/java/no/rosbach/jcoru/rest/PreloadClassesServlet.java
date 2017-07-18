package no.rosbach.jcoru.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class PreloadClassesServlet implements ServletContextListener {
    public static final String PRELOAD_CLASSES_FILE = "preload-classes.txt";
    private static final Logger LOGGER = LoggerFactory.getLogger(PreloadClassesServlet.class);

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
