package no.rosbach.edu.compile.fixtures;

import static java.util.stream.Collectors.toList;
import static no.rosbach.edu.utils.Stream.stream;

import no.rosbach.edu.filemanager.JavaSourceString;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by mapster on 05.04.15.
 */
public enum Fixtures {
  AGGREGATION_CLASS("AggregationClass"), CONTAINED_CLASS("ContainedClass"), TEST_CLASS("TestClass"), FAIL_TEST("FailTest"), SUCCESS_TEST("SuccessTest"),
  TEST_SUBJECT("TestSubject"), TEST_SUBJECT_TEST("TestSubjectTest"), ILLEGAL_SYNTAX(
      "IllegalSyntax",
      true), NOT_REALLY_TEST("NotReallyTest"), NOT_NAMED_AS_TEST_CLASS(
      "NotNamedAsTestClass");

  private final String name;
  private final boolean notCompilable;

  Fixtures(String className) {
    this.name = className;
    notCompilable = false;
  }

  Fixtures(String className, boolean notCompilable) {
    name = className;
    this.notCompilable = notCompilable;
  }

  public static List<JavaSourceString> getFixtureSources(Fixtures... fixtures) {
    return stream(fixtures).map(Fixtures::getFixtureSource).collect(toList());
  }

  public static JavaSourceString getFixtureSource(Fixtures fixture) {
    String fileName = fixture + ".java";
    try (InputStream sourceStream = Fixtures.class.getClassLoader().getResourceAsStream(getSourceDirectory(fixture) + File.separatorChar + fileName)) {
      return new JavaSourceString(fileName, IOUtils.toString(sourceStream));
    } catch (IOException e) {
      throw new Error("Failed to read fixture java source.", e);
    }
  }

  public static JavaSourceString getFixtureInterfaceSource(Fixtures className) {
    String fileName = className + ".java";
    try (InputStream sourceStream = new FileInputStream(
        compactPath(
            "src",
            "test",
            "java",
            "no",
            "rosbach",
            "edu",
            "compile",
            "fixtures",
            fileName))) {
      return new JavaSourceString(fileName, IOUtils.toString(sourceStream));
    } catch (FileNotFoundException e) {
      throw new Error(String.format("Could not find interface source for fixture %s", className), e);
    } catch (IOException e) {
      throw new Error(String.format("Failed to read interface for fixture %s", className), e);
    }
  }

  private static String getSourceDirectory(Fixtures f) {
    if (f.notCompilable) {
      return "uncompilable-fixtures";
    }
    return "fixtures";
  }

  private static String compactPath(String... path) {
    return stream(path).reduce((s1, s2) -> s1 + File.separatorChar + s2).get();
  }

  @Override
  public String toString() {
    return name;
  }
}
