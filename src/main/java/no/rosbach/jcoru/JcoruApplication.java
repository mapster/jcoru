package no.rosbach.jcoru;

import no.rosbach.jcoru.rest.PreloadClasses;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JcoruApplication {
    /**
     * Run application.
     * @param args arguments.s
     */
    public static void main(String[] args) {
        SpringApplication.run(JcoruApplication.class, args);

        PreloadClasses preloadClasses = new PreloadClasses();
        preloadClasses.loadClasses();
    }
}
