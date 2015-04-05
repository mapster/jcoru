package no.rosbach.edu.compiler.fixtures;

import no.rosbach.edu.compiler.JavaSourceString;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mapster on 05.04.15.
 */
public enum Fixtures {
    AGGREGATION_CLASS("AggregationClass"), CONTAINED_CLASS("ContainedClass"), TEST_CLASS("TestClass"), UNIT_TEST("UnitTest");

    private final String name;

    Fixtures(String className) {
        this.name = className;
    }

    @Override
    public String toString() {
        return name;
    }

    public static JavaSourceString getFixtureSource(Fixtures className) {
        String fileName = className + ".java";
        try(InputStream sourceStream = Fixtures.class.getClassLoader().getResourceAsStream("fixtures" + File.separatorChar + fileName)) {
            return new JavaSourceString(fileName, IOUtils.toString(sourceStream));
        } catch (IOException e) {
            throw new Error("Failed to read fixture java source.", e);
        }
    }
}
