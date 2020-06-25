package org.folio.validate.value.format;

import org.apache.commons.validator.routines.RegexValidator;

public class NumberFormatValidator implements FormatValidator {

  public static final String INVALID_FORMAT_MESSAGE = "Invalid Number format: %s";

  private static final RegexValidator VALIDATOR;

  static {
    String[] formats = {
      "(-?\\d+)",                              // 00000 | -00000
      "(-?\\d*[,|\\.]\\d+)",                   // #,0000 | 0,0000 | -#,0000 | -0,0000 | #.0000 | 0.0000 | -#.0000 | -0.0000
      "(-?\\d{1,3}(\\s\\d{3})+([,|\\.]\\d+)?)",// 0 000,## | 0 000.## | -0 000,## | 0 000.##
      "(-?\\d{1,3}(,\\d{3})+(\\.\\d+)?)"       // 0,000.## | -0,000.##
    };
    VALIDATOR = new RegexValidator(formats);
  }

  @Override
  public void validate(String value) {
    if (!VALIDATOR.isValid(value)) {
      throw new IllegalArgumentException(String.format(INVALID_FORMAT_MESSAGE, value));
    }
  }
}
