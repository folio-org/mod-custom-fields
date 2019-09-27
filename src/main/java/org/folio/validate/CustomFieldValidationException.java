package org.folio.validate;

import org.folio.rest.jaxrs.model.Errors;

public class CustomFieldValidationException extends RuntimeException {

  private Errors errors;

  public CustomFieldValidationException(Errors errors) {
    this.errors = errors;
  }

  public Errors getErrors() {
    return errors;
  }
}
