package org.folio.validate;

import org.folio.rest.jaxrs.model.Error;
import org.folio.rest.jaxrs.model.Parameter;

public class ValidationUtil {

  private ValidationUtil() {
  }

  public static Error createError(String value, String fieldName, String message) {
    Error error = new Error();
    Parameter p = new Parameter();
    p.setKey(fieldName);
    p.setValue(value);
    error.getParameters().add(p);
    error.setMessage(message);
    error.setCode("-1");
    error.setType("1");
    return error;
  }

}
