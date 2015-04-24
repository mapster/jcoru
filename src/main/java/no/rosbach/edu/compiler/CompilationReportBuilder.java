package no.rosbach.edu.compiler;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.SimpleJavaFileObject;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by mapster on 24.04.15.
 */
public class CompilationReportBuilder implements DiagnosticListener<SimpleJavaFileObject> {

    private final List<Diagnostic<? extends SimpleJavaFileObject>> reports = new LinkedList<>();

    @Override
    public void report(Diagnostic<? extends SimpleJavaFileObject> diagnostic) {
        System.out.println("code: "+diagnostic.getCode());
        System.out.println("kind: "+diagnostic.getKind());
        System.out.println("msg: "+diagnostic.getMessage(null));
        reports.add(diagnostic);
    }

    public CompilationReport buildReport() {
        return new CompilationReport(reports.stream().map(d -> new CompilationReportEntry(d)).collect(toList()));
    }
}
