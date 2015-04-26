package no.rosbach.edu.rest.facade;

import javax.ws.rs.Path;

/**
 * Created by mapster on 26.04.15.
 */
@Path(JUnitRunnerResource.TEST_PATH)
public class JUnitRunnerResource {
    public static final String TEST_PATH = "/test";
//
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public CompilationReport compile(List<JavaSourceStringDTO> javaSources) throws IOException {
//        throwBadRequestIfSourcesAreInvalid(javaSources);
//        List<JavaSourceString> collect = javaSources.stream().map(source -> source.transfer()).collect(toList());
//        compiler.compile(collect);
//
//        return reportBuilder.buildReport();
//    }
}
