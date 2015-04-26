package no.rosbach.edu.rest.reports;

import no.rosbach.edu.compile.JUnitRunnerTestBase;
import no.rosbach.edu.compile.fixtures.Fixtures;
import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static no.rosbach.edu.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertEquals;

/**
 * Created by mapster on 26.04.15.
 */
public class JUnitReportFailureTest extends JUnitRunnerTestBase {

    private JUnitReport jUnitReport;
    private Result result;
    private Failure resultCmpFailure;
    private JUnitReportFailure reportCmpFailure;

    @Before
    public void prepare() {
        result = runTests(compileAndLoadClasses(getFixtureSource(Fixtures.FAIL_TEST)));
        resultCmpFailure = result.getFailures().stream().filter(JUnitReportFailureTest::isComparisonFailure).findFirst().orElseThrow(() -> new Error("Fixture contains no failing tests."));
        jUnitReport = new JUnitReport(result);
        reportCmpFailure = jUnitReport.getFailures().stream().filter(this::isSameFailureAsResultCmpFailure).findFirst().orElseThrow(() -> new Error("Failure not copied into report."));
    }

    private static boolean isComparisonFailure(Failure f) {
        return f.getException() instanceof ComparisonFailure;
    }

    private boolean isSameFailureAsResultCmpFailure(JUnitReportFailure f) {
        return String.format("%s(%s)",f.getTestMethodName(), f.getTestClassName()).equals(resultCmpFailure.getTestHeader());
    }

    @Test
    public void shouldCopyTestClassNameFromDescription() {
        assertEquals(resultCmpFailure.getDescription().getClassName(), reportCmpFailure.getTestClassName());
    }

    @Test
    public void shouldCopyTestMethodNameFromDescription() {
        assertEquals(resultCmpFailure.getDescription().getMethodName(), reportCmpFailure.getTestMethodName());
    }

    @Test
    public void shouldCopyFailureType() {
        assertEquals(resultCmpFailure.getException().getClass().getSimpleName(), reportCmpFailure.getFailureType());
    }

    @Test
    public void shouldCopyExpectedForComparisonFailure() {
        ComparisonFailure exception = (ComparisonFailure) resultCmpFailure.getException();
        assertEquals(exception.getExpected(), reportCmpFailure.getExpected());
    }

    @Test
    public void shouldCopyActualForComparisonFailure() {
        ComparisonFailure exception = (ComparisonFailure) resultCmpFailure.getException();
        assertEquals(exception.getActual(), reportCmpFailure.getActual());
    }
}
