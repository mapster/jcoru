package no.rosbach.edu.compiler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path(CompilerResource.COMPILER_PATH)
public class CompilerResource {
    public static final String COMPILER_PATH = "/compile";

    private final CompilationReportBuilder reportBuilder = new CompilationReportBuilder();
    MyCompiler compiler = new MyCompiler(reportBuilder);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CompilationReport compile(List<JavaSourceStringDTO> javaSources) throws IOException {
        throwBadRequestIfSourcesAreInvalid(javaSources);
        List<JavaSourceString> collect = javaSources.stream().map(source -> source.transfer()).collect(toList());
        compiler.compile(collect);

        return reportBuilder.buildReport();
    }

    private void throwBadRequestIfSourcesAreInvalid(List<JavaSourceStringDTO> sources) {
        List<JavaSourceStringDTO> invalidSources = sources.stream().filter(s -> isNotBlank(s.filename) || isNotBlank(s.sourcecode)).collect(toList());
        if(!invalidSources.isEmpty()) {
            throw new BadRequestException(String.format("The following source files are missing file name or source code: ", invalidSources.stream().map(s -> s.filename).reduce((f1, f2) -> f1 + ", " + f2)));
        }
    }

}
