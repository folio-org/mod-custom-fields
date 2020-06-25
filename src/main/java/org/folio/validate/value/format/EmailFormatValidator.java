package org.folio.validate.value.format;

import org.apache.commons.validator.routines.EmailValidator;

public class EmailFormatValidator implements FormatValidator {

  private static final String INVALID_FORMAT_MESSAGE = "Invalid Email format: %s";

  private static final EmailValidator VALIDATOR = EmailValidator.getInstance();

  @Override
  public void validate(String value) {
    if (!VALIDATOR.isValid(value)) {
      throw new IllegalArgumentException(String.format(INVALID_FORMAT_MESSAGE, value));
    }
  }
}
