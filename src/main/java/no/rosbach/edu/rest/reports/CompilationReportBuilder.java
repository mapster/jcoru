package no.rosbach.edu.rest.reports;

import java.util.LinkedList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.SimpleJavaFileObject;

import static java.util.stream.Collectors.toList;

/**
 * Created by mapster on 24.04.15.
 */
public class CompilationReportBuilder implements DiagnosticListener<SimpleJavaFileObject> {

    private final List<Diagnostic<? extends SimpleJavaFileObject>> reports = new LinkedList<>();

    @Override
    public void report(Diagnostic<? extends SimpleJavaFileObject> diagnostic) {
        reports.add(diagnostic);
    }

    public CompilationReport buildReport() {
        return new CompilationReport(reports.stream().map(d -> new CompilationReportEntry(d)).collect(toList()));
    }
}
