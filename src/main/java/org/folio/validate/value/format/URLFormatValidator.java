package org.folio.validate.value.format;

import java.net.MalformedURLException;
import java.net.URL;

public class URLFormatValidator implements FormatValidator {

  private static final String INVALID_FORMAT_MESSAGE = "Invalid URL: %s";

  @Override
  public void validate(String value) {
    try {
      new URL(value);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(String.format(INVALID_FORMAT_MESSAGE, value));
    }
  }
}
