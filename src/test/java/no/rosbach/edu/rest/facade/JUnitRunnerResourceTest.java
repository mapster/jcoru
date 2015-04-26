package no.rosbach.edu.rest.facade;

import no.rosbach.edu.rest.reports.CompilationReport;
import no.rosbach.edu.rest.reports.Report;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

/**
 * Created by mapster on 26.04.15.
 */
public class JUnitRunnerResourceTest extends CompilerResourceTestBase {

    @Override
    protected Class[] getFacadesToTest() {
        return new Class[]{JUnitRunnerResource.class};
    }

    @Override
    protected Invocation.Builder request() {
        return target(JUnitRunnerResource.TEST_PATH).request();
    }

    @Override
    protected CompilationReport compilationReportFromResponse(Response response) {
        return response.readEntity(Report.class).compilationReport;
    }

}
