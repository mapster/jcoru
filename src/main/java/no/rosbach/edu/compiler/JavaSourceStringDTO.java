package no.rosbach.edu.compiler;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mapster on 05.04.15.
 */
@XmlRootElement
public class JavaSourceStringDTO {

    public String filename;
    public String sourcecode;

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

        if(other.filename == null) {
            if(filename != null) {
                return false;
            }
        }
        else if(!other.filename.equals(filename)) {
            return false;
        }

        if(other.sourcecode == null) {
            return sourcecode == null;
        }
        else {
            return other.sourcecode.equals(sourcecode);
        }
    }
}
