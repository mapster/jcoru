package no.rosbach.edu.compiler;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import java.util.Locale;

/**
 * Created by mapster on 16.03.15.
 */
public class SensitiveDiagnosticListener implements DiagnosticListener {
    @Override
    public void report(Diagnostic diagnostic) {
        throw new CompilationError("Failed to compile:  " +diagnostic.getMessage(Locale.ENGLISH));
    }

    public static class CompilationError extends Error {
        public CompilationError(String message) {
            super(message);
        }
    }
}
