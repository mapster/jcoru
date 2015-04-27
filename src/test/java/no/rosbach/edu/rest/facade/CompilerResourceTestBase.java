package no.rosbach.edu.rest.facade;

import static no.rosbach.edu.compile.fixtures.Fixtures.getFixtureInterfaceSource;
import static no.rosbach.edu.compile.fixtures.Fixtures.getFixtureSource;
import static no.rosbach.edu.utils.Stream.stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import no.rosbach.edu.compile.fixtures.Fixtures;
import no.rosbach.edu.filemanager.JavaSourceString;
import no.rosbach.edu.rest.ErrorMessage;
import no.rosbach.edu.rest.JavaSourceStringDto;
import no.rosbach.edu.rest.reports.CompilationReport;

import org.apache.commons.lang3.ArrayUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by mapster on 26.04.15.
 */
public abstract class CompilerResourceTestBase extends JerseyTest {

  protected static final JavaSourceStringDto TEST_CLASS_SOURCE = new JavaSourceStringDto(getFixtureSource(Fixtures.TEST_CLASS));
  protected static final JavaSourceStringDto TEST_CLASS_I_SOURCE = new JavaSourceStringDto(getFixtureInterfaceSource(Fixtures.TEST_CLASS));
  private static final Class[] RESPONSE_MAPPERS = {DefaultExceptionMapper.class};

  protected abstract Class[] getFacadesToTest();

  protected abstract Invocation.Builder request();

  protected abstract CompilationReport compilationReportFromResponse(Response response);

  @Override
  final protected Application configure() {
    return new ResourceConfig(ArrayUtils.addAll(getFacadesToTest(), RESPONSE_MAPPERS));
  }

  protected Response stringRequest(String s) {
    Entity entity = null;
    if (s != null) {
      entity = Entity.entity(s, MediaType.APPLICATION_JSON);
    }
    return request().post(entity);
  }

  protected void expectMessageAndException(Response response, Class<? extends WebApplicationException> expectedException) {
    try {
      ErrorMessage msg = response.readEntity(ErrorMessage.class);
      assertEquals(response.getStatus(), msg.status);
    } catch (Exception e) {
      fail();
    }
    try {
      ResponseHandler.throwExceptionIfError(response);
    } catch (WebApplicationException thrown) {
      assertEquals(expectedException, thrown.getClass());
    }
  }

  /**
   * Perform a request where CompilationReport is the expected response.
   *
   * @param javaSources the java sources to compile.
   * @return a report of the compilation.
   */
  protected CompilationReport compilationReportRequest(JavaSourceString... javaSources) {
    return compilationReportRequest(stream(javaSources).map(JavaSourceStringDto::new).toArray(JavaSourceStringDto[]::new));
  }

  /**
   * Perform a request where CompilationReport is the expected response.
   *
   * @param javaSources the java sources to compile.
   * @return a report of the compilation.
   */
  protected CompilationReport compilationReportRequest(JavaSourceStringDto... javaSources) {
    List<JavaSourceStringDto> entity = Arrays.asList(javaSources);
    GenericEntity<List<JavaSourceStringDto>> listGenericEntity = new GenericEntity<List<JavaSourceStringDto>>(entity) {
    };

    Response response = request().post(Entity.entity(listGenericEntity, MediaType.APPLICATION_JSON));
    ResponseHandler.throwExceptionIfError(response);

    return compilationReportFromResponse(response);
  }

  @Test
  public void compilerReturnsFailedReport() {
    CompilationReport result = compilationReportRequest(TEST_CLASS_SOURCE);
    assertFalse(result.isSuccess());
  }

  @Test(expected = BadRequestException.class)
  public void returnsBadRequestForJavaFileMissingSourceString() {
    compilationReportRequest(new JavaSourceStringDto("TestClass.java", null));
  }

  @Test(expected = BadRequestException.class)
  public void returnsBadRequestForJavaFileMissingFileName() {
    compilationReportRequest(new JavaSourceStringDto("", "This is some java source"));
  }

  @Test
  public void returnsBadRequestForNullPost() {
    expectMessageAndException(stringRequest(null), BadRequestException.class);
  }

  @Test
  public void returnsBadRequestForEmptyStringPost() {
    expectMessageAndException(stringRequest(""), BadRequestException.class);
  }

  @Test
  public void returnsBadRequestForInvalidJson() {
    expectMessageAndException(stringRequest("{\"field:\" 100}"), BadRequestException.class);
  }

  @Test
  public void returnsFailingReportForIllegalSyntax() {
    CompilationReport report = compilationReportRequest(getFixtureSource(Fixtures.ILLEGAL_SYNTAX));
    assertFalse(report.isSuccess());
  }

  @Test
  public void returnsFailingReportWhenReferencedClassIsMissing() {
    CompilationReport compilationReport = compilationReportRequest(getFixtureSource(Fixtures.TEST_SUBJECT_TEST));
    assertFalse(compilationReport.isSuccess());
  }
}
