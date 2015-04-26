package no.rosbach.edu.rest.reports;

import org.junit.runner.Result;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by mapster on 26.04.15.
 */
public class JUnitReport {
    private int ignored;
    private long runTime;
    private int failedTests;
    private int tests;
    private List<JUnitReportFailure> failures;

    /**
     * The default constructor.
     */
    public JUnitReport() {
    }

    /**
     * Construct a report from Junit Runner Result.
     * @param junitResult
     */
    public JUnitReport(Result junitResult) {
        tests = junitResult.getRunCount();
        failedTests = junitResult.getFailureCount();
        ignored = junitResult.getIgnoreCount();
        runTime = junitResult.getRunTime();
        failures = junitResult.getFailures().stream().map(f -> new JUnitReportFailure(f)).collect(toList());
    }

    /**
     * The number of ignored tests.
     * @return ignored tests.
     */
    public int getIgnored() {
        return ignored;
    }

    /**
     * Set the number of ignored tests.
     * @param ignored ignored tests.
     */
    public void setIgnored(int ignored) {
        this.ignored = ignored;
    }

    /**
     * The run time of the test suite in milliseconds.
     * @return run time in ms.
     */
    public long getRunTime() {
        return runTime;
    }

    /**
     * Set the run time of the test suite in milliseconds.
     * @param runTime
     */
    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    /**
     * The number of failed tests.
     * @return failed tests.
     */
    public int getFailedTests() {
        return failedTests;
    }

    /**
     * Set the number of failed tests.
     * @param failedTests
     */
    public void setFailedTests(int failedTests) {
        this.failedTests = failedTests;
    }

    /**
     * The total number of tests run.
     * @return total tests.
     */
    public int getTests() {
        return tests;
    }

    /**
     * Set the total number of tests run.
     * @param tests
     */
    public void setTests(int tests) {
        this.tests = tests;
    }

    public List<JUnitReportFailure> getFailures() {
        return failures;
    }

}
