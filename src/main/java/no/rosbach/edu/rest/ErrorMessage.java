package no.rosbach.edu.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mapster on 25.04.15.
 */
@XmlRootElement
public class ErrorMessage {
  public int status;
  public String message;
  public String developerMessage;
}
