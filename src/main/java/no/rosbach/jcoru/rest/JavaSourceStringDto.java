package no.rosbach.jcoru.rest;

import no.rosbach.jcoru.filemanager.JavaSourceString;

public class JavaSourceStringDto {

  public String filename;
  public String sourcecode;

  public JavaSourceStringDto() {
  }

  public JavaSourceStringDto(String filename, String sourcecode) {
    this.filename = filename;
    this.sourcecode = sourcecode;
  }

  public JavaSourceStringDto(JavaSourceString toTransfer) {
    this.filename = toTransfer.getName();
    this.sourcecode = toTransfer.getCharContent(true).toString();
  }

  public static JavaSourceString create(JavaSourceStringDto source) {
    return source.transfer();
  }

  public JavaSourceString transfer() {
    return new JavaSourceString(filename, sourcecode);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof JavaSourceStringDto)) {
      return false;
    }

    JavaSourceStringDto other = (JavaSourceStringDto) obj;

    if (other.filename == null) {
      if (filename != null) {
        return false;
      }
    } else if (!other.filename.equals(filename)) {
      return false;
    }

    if (other.sourcecode == null) {
      return sourcecode == null;
    } else {
      return other.sourcecode.equals(sourcecode);
    }
  }
}
