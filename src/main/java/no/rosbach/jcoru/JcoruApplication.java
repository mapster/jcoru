package no.rosbach.jcoru;

import no.rosbach.jcoru.rest.PreloadClasses;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextListener;

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



    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
}
