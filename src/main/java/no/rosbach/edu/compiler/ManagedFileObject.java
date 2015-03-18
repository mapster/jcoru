package no.rosbach.edu.compiler;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URI;

/**
 * Created by mapster on 18.03.15.
 */
public class ManagedFileObject implements JavaFileObject {
    private final JavaFileManager fileManager;
    private final JavaFileObject fileObject;

    public ManagedFileObject(JavaFileManager fileManager, JavaFileObject fileObject) {
        this.fileManager = fileManager;
        this.fileObject = fileObject;
    }

    public JavaFileManager getFileManager() {
        return fileManager;
    }

    public JavaFileObject getFileObject() {
        return fileObject;
    }

    @Override
    public Kind getKind() {
        return fileObject.getKind();
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        return fileObject.isNameCompatible(simpleName, kind);
    }

    @Override
    public NestingKind getNestingKind() {
        return fileObject.getNestingKind();
    }

    @Override
    public Modifier getAccessLevel() {
        return fileObject.getAccessLevel();
    }

    @Override
    public URI toUri() {
        return fileObject.toUri();
    }

    @Override
    public String getName() {
        return fileObject.getName();
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return fileObject.openInputStream();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return fileObject.openOutputStream();
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        return fileObject.openReader(ignoreEncodingErrors);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return fileObject.getCharContent(ignoreEncodingErrors);
    }

    @Override
    public Writer openWriter() throws IOException {
        return fileObject.openWriter();
    }

    @Override
    public long getLastModified() {
        return fileObject.getLastModified();
    }

    @Override
    public boolean delete() {
        return fileObject.delete();
    }
}
