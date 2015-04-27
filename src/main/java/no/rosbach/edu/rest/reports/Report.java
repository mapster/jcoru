package no.rosbach.edu.rest.reports;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mapster on 26.04.15.
 */
@XmlRootElement
public class Report {
    public JUnitReport jUnitReport;
    public CompilationReport compilationReport;

    public Report() {
    }

    public Report(JUnitReport report) {
        this.jUnitReport = report;
    }
    public Report(CompilationReport report) {
        this.compilationReport = report;
    }

}