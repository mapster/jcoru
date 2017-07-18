package no.rosbach.jcoru.rest.facade;

import no.rosbach.jcoru.rest.JavaSourceStringDto;
import no.rosbach.jcoru.rest.reports.CompilationReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CompilerResourceTest extends CompilerResourceTestBase {
  @Resource
  private CompilerResource compilerResource;

  @Test
  public void compilerReturnsSuccessReport() {
    CompilationReport result = compile(Arrays.asList(TEST_CLASS_SOURCE, TEST_CLASS_I_SOURCE));
    assertTrue(result.isSuccess());
  }

  @Test
  public void returnsSuccessForEmptyList() {
    CompilationReport report = compile(Collections.emptyList());
    assertTrue(report.isSuccess());
  }

  @Override
  protected CompilationReport compile(List<JavaSourceStringDto> sources) {
    return compilerResource.compilePost(sources);
  }
}
