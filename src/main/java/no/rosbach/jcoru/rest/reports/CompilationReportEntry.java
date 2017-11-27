package no.rosbach.jcoru.rest.reports;

import javax.tools.Diagnostic;
import javax.tools.SimpleJavaFileObject;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Optional;

public class CompilationReportEntry {
  public String message;
  public String code;
  public long lineNumber;
  public long columnNumber;
  public String sourceName;
  private Diagnostic.Kind kind;

  public CompilationReportEntry() {

  }

  /**
   * Create a complete report entry from Diagnostic.
   *
   * @param diagnostic the diagnostic.
   */
  public CompilationReportEntry(Diagnostic<? extends SimpleJavaFileObject> diagnostic) {
    Optional<? extends Diagnostic<? extends SimpleJavaFileObject>> optional = Optional.of(diagnostic);
    message = optional.map(d -> d.getMessage(null)).orElse(null);
    code = optional.map(Diagnostic::getCode).orElse(null);
    kind = optional.map(Diagnostic::getKind).orElse(null);
    lineNumber = optional.map(Diagnostic::getLineNumber).orElse(null);
    columnNumber = optional.map(Diagnostic::getColumnNumber).orElse(null);
    sourceName = optional.map(Diagnostic::getSource).map(SimpleJavaFileObject::getName).orElse(null);
  }

  public String getKind() {
    return kind.name();
  }

  public void setKind(String kind) {
    this.kind = Diagnostic.Kind.valueOf(kind);
  }

  @XmlTransient
  public Diagnostic.Kind getDiagnosticKind() {
    return kind;
  }
}
