package no.rosbach.edu.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

/**
* Created by mapster on 26.11.14.
*/
class JavaSourceString extends SimpleJavaFileObject {
    private String sourcecode;

    protected JavaSourceString(String filename, String sourcecode){
        super(URI.create(filename), Kind.SOURCE);
        this.sourcecode = sourcecode;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return sourcecode;
    }
}
