package no.rosbach.edu.filemanager;

/**
 * Created by mapster on 05.04.15.
 */
public class UnsupportedLocation extends RuntimeException {
    public UnsupportedLocation(String msg) {
        super(msg);
    }
}