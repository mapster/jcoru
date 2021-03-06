package no.rosbach.jcoru.rest.reports;

import java.util.List;

import javax.tools.Diagnostic;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CompilationReport {
  public List<CompilationReportEntry> entries;
  private Diagnostic.Kind reportLevel;

  public CompilationReport() {
  }

  public CompilationReport(List<CompilationReportEntry> reports) {
    entries = reports;
    reportLevel = reports.stream().map(d -> d.getDiagnosticKind()).min((k1, k2) -> k1.compareTo(k2)).orElse(null);
  }

  public String getReportLevel() {
    if (reportLevel == null) {
      return "SUCCESS";
    }
    return reportLevel.name();
  }

  public void setReportLevel(String level) {
    this.reportLevel = Diagnostic.Kind.valueOf(level);
  }

  public boolean isSuccess() {
    return reportLevel == null || !reportLevel.equals(Diagnostic.Kind.ERROR);
  }
}
