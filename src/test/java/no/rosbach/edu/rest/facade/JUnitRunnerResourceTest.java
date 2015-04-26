package no.rosbach.edu.rest.facade;

/**
 * Created by mapster on 26.04.15.
 */
public class JUnitRunnerResourceTest extends ResourceTestBase {


    @Override
    protected Class[] getFacadesToTest() {
        return new Class[]{JUnitRunnerResource.class};
    }
}
