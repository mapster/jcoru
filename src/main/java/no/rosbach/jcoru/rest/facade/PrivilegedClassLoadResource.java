package no.rosbach.jcoru.rest.facade;

import no.rosbach.jcoru.rest.reports.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/loadclass")
public class PrivilegedClassLoadResource {

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity loadClass(@RequestParam("name") String name) {

        try {
            Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new NotFoundException("Could not load class.", e);
        }
        return ResponseEntity.noContent().build();
    }
}
