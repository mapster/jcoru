package no.rosbach.edu.compiler;

/**
 * Created by mapster on 05.04.15.
 */
public class JavaSourceStringDTO {

    private String filename;
    private String sourcecode;

    public JavaSourceStringDTO() {}

    public JavaSourceStringDTO(String filename, String sourcecode) {
        this.filename = filename;
        this.sourcecode = sourcecode;
    }

    public JavaSourceStringDTO(JavaSourceString toTransfer) {
        this.filename = toTransfer.getName();
        this.sourcecode = toTransfer.getCharContent(true).toString();
    }

    public JavaSourceString transfer() {
        return new JavaSourceString(filename, sourcecode);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof JavaSourceStringDTO)) {
            return false;
        }

        JavaSourceStringDTO other = (JavaSourceStringDTO) o;
        if(!other.filename.equals(filename)) {
            return false;
        }
        return other.sourcecode.equals(sourcecode);
    }
}
