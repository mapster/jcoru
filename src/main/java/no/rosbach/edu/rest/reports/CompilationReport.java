package no.rosbach.edu.rest.reports;

import javax.tools.Diagnostic;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by mapster on 24.04.15.
 */
@XmlRootElement
public class CompilationReport {
    private Diagnostic.Kind reportLevel;
    public List<CompilationReportEntry> entries;

    public CompilationReport() {
    }

    public CompilationReport(List<CompilationReportEntry> reports) {
        entries = reports;
        reportLevel = reports.stream().map(d -> d.getDiagnosticKind()).min((k1, k2) -> k1.compareTo(k2)).orElse(null);
    }

    public String getReportLevel() {
        if(reportLevel == null) {
            return null;
        }
       return reportLevel.name();
    }

    public void setReportLevel(String level) {
        this.reportLevel = Diagnostic.Kind.valueOf(level);
    }

    public boolean isSuccess() {
        return entries.isEmpty();
    }
}
