package no.rosbach.jcoru.rest.reports;

import javax.tools.Diagnostic;
import javax.tools.SimpleJavaFileObject;
import javax.xml.bind.annotation.XmlTransient;

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
    message = diagnostic.getMessage(null);
    code = diagnostic.getCode();
    kind = diagnostic.getKind();
    lineNumber = diagnostic.getLineNumber();
    columnNumber = diagnostic.getColumnNumber();
    sourceName = diagnostic.getSource().getName();
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
