package no.rosbach.edu.filemanager;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * Created by mapster on 26.11.14.
 */
public class JavaSourceString extends SimpleJavaFileObject {
    private String sourcecode;

    public JavaSourceString(String filename, String sourcecode) {
        super(URI.create(filename), Kind.SOURCE);
        this.sourcecode = sourcecode;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return sourcecode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof JavaSourceString)) {
            return false;
        }

        JavaSourceString other = (JavaSourceString) o;
        if (other.toUri() == null) {
            if (toUri() != null) {
                return false;
            }
        } else if (!other.toUri().equals(toUri())) {
            return false;
        }

        if (other.sourcecode == null) {
            return sourcecode == null;
        } else {
            return other.sourcecode.equals(sourcecode);
        }
    }
}
