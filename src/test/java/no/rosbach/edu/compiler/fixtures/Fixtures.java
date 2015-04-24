package no.rosbach.edu.compiler.fixtures;

import no.rosbach.edu.compiler.JavaSourceString;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;
import java.util.stream.StreamSupport;

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

    public static JavaSourceString getFixtureInterfaceSource(Fixtures className) {
        String fileName = className + ".java";
        try (InputStream sourceStream = new FileInputStream(compactPath("src","test","java","no","rosbach","edu","compiler","fixtures", fileName))) {
            return new JavaSourceString(fileName, IOUtils.toString(sourceStream));
        } catch (FileNotFoundException e) {
            throw new Error(String.format("Could not find interface source for fixture %s", className), e);
        } catch (IOException e) {
            throw new Error(String.format("Failed to read interface for fixture %s", className), e);
        }
    }

    private static String compactPath(String... path) {
        return StreamSupport.stream(Arrays.spliterator(path), false).reduce((s1, s2) -> s1 + File.separatorChar + s2).get();
    }
}
