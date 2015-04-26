package no.rosbach.edu.rest.reports;

import no.rosbach.edu.compile.JUnitRunnerTestBase;
import no.rosbach.edu.compile.fixtures.Fixtures;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;

import static no.rosbach.edu.compile.fixtures.Fixtures.getFixtureSource;
import static org.junit.Assert.assertEquals;

/**
 * Created by mapster on 26.04.15.
 */
public class JUnitReportTest extends JUnitRunnerTestBase {

    private JUnitReport jUnitReport;
    private Result result;

    @Before
    public void prepare() {
        result = runTests(compileAndLoadClasses(getFixtureSource(Fixtures.FAIL_TEST)));
        jUnitReport = new JUnitReport(result);
    }

    @Test
    public void shouldCopyRunCount() {
        assertEquals(result.getRunCount(), jUnitReport.getTests());
    }

    @Test
    public void shouldCopyFailureCount() {
        assertEquals(result.getFailureCount(), jUnitReport.getFailedTests());
    }

    @Test
    public void shouldCopyRunTime() {
        assertEquals(result.getRunTime(), jUnitReport.getRunTime());
    }

    @Test
    public void shouldCopyIgnored() {
        assertEquals(result.getIgnoreCount(), jUnitReport.getIgnored());
    }

    @Test
    public void shouldCreateFailuresFromResult() {
        assertEquals(result.getFailureCount(), jUnitReport.getFailures().size());
    }
}
