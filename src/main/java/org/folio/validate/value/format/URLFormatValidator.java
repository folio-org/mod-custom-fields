package org.folio.validate.value.format;

import org.apache.commons.validator.routines.UrlValidator;

public class URLFormatValidator implements FormatValidator {

  private static final String INVALID_FORMAT_MESSAGE = "Invalid URL: %s";

  private static final UrlValidator VALIDATOR = new UrlValidator(new String[] {"http", "https"});

  @Override
  public void validate(String value) {
    if (!VALIDATOR.isValid(value)) {
      throw new IllegalArgumentException(String.format(INVALID_FORMAT_MESSAGE, value));
    }
  }
}
