package no.rosbach.edu.rest;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorMessage {
  public int status;
  public String message;
  public String developerMessage;
}
