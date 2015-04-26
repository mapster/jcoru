package no.rosbach.edu.rest.reports;

/**
 * Created by mapster on 26.04.15.
 */
public interface Report {
    enum Kind {
        COMPILATION, JUnit
    }

    Kind getKind();
}
