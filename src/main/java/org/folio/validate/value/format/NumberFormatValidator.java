package org.folio.validate.value.format;

import java.util.StringJoiner;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class NumberFormatValidator implements FormatValidator {

  public static final String INVALID_FORMAT_MESSAGE = "Invalid Number format: %s";

  private static final Pattern NUMBER_FORMAT;

  static {
    String[] formats = {
      "(-?\\d+)",                              // 00000 | -00000
      "(-?\\d*[,|\\.]\\d+)",                   // #,0000 | 0,0000 | -#,0000 | -0,0000 | #.0000 | 0.0000 | -#.0000 | -0.0000
      "(-?\\d{1,3}(\\s\\d{3})+([,|\\.]\\d+)?)",// 0 000,## | 0 000.## | -0 000,## | 0 000.##
      "(-?\\d{1,3}(,\\d{3})+(\\.\\d+)?)"       // 0,000.## | -0,000.##
    };
    StringJoiner joiner = new StringJoiner("|", "^", "$");
    for (String format : formats) {
      joiner.add(format);
    }
    NUMBER_FORMAT = Pattern.compile(joiner.toString());
  }

  @Override
  public void validate(String value) {
    if (StringUtils.isBlank(value) || !NUMBER_FORMAT.matcher(value).matches()) {
      throw new IllegalArgumentException(String.format(INVALID_FORMAT_MESSAGE, value));
    }
  }
}
