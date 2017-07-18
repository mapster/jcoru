package no.rosbach.jcoru.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class PreloadClasses {
    public static final String PRELOAD_CLASSES_FILE = "preload-classes.txt";
    private static final Logger LOGGER = LoggerFactory.getLogger(PreloadClasses.class);

    public void loadClasses() {
        LOGGER.info("Pre-loading classes.");
        boolean success = true;
        try (InputStream classesStream = getClass().getClassLoader().getResourceAsStream(PRELOAD_CLASSES_FILE)) {
            Scanner sc = new Scanner(classesStream);
            while (sc.hasNextLine()) {
                String name = sc.nextLine().trim();
                if (!isEmpty(name)) {
                    try {
                        Class.forName(name);
                    } catch (ClassNotFoundException e) {
                        success = false;
                        LOGGER.error(String.format("Failed to pre-load class %s", name), e);
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.error("Failed to pre-load classes.", e);
        }
        if (success) {
            LOGGER.info("Successfully pre-loaded all classes.");
        } else {
            LOGGER.info("Some (or all) classes were not pre-loaded.");
        }
    }

}
