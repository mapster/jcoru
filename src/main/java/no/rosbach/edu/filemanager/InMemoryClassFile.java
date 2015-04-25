package no.rosbach.edu.filemanager;


import org.apache.commons.io.IOUtils;

import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;

/**
 * Created by mapster on 08.03.15.
 */
public class InMemoryClassFile extends SimpleJavaFileObject {

    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    /**
     * Construct an InMemoryClassFile with the given URI.
     *
     * @param name the name for this file object.
     */
    protected InMemoryClassFile(String name) {
        super(URI.create(name), Kind.CLASS);
    }

    public InMemoryClassFile(URI uri, InputStream inputStream) throws IOException {
        super(uri, Kind.CLASS);
        IOUtils.copy(inputStream, out);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return out;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        out.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }
}