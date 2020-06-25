package org.folio.validate.value.format;

import org.apache.commons.lang3.StringUtils;

public class TextFormatValidator implements FormatValidator {

  public static final String INVALID_FORMAT_MESSAGE = "Invalid Text format";

  @Override
  public void validate(String value) {
    if (StringUtils.isBlank(value)) {
      throw new IllegalArgumentException(INVALID_FORMAT_MESSAGE);
    }
  }
}
