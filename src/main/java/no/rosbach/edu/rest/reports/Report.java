package no.rosbach.edu.rest.reports;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Report {
  public JUnitReport junitReport;
  public CompilationReport compilationReport;

  public Report() {
  }

  public Report(JUnitReport report) {
    this.junitReport = report;
  }

  public Report(CompilationReport report) {
    this.compilationReport = report;
  }

}