package org.folio.validate.value.format;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class EmailFormatValidator implements FormatValidator {

  private static final String INVALID_FORMAT_MESSAGE = "Invalid Email format: %s";

  private static final String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
  private static final String DOMAIN = ATOM + "+(\\." + ATOM + "+)*";
  private static final String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}]";
  private static final int MAX_LOCAL_PART_LENGTH = 64;
  private static final int MAX_DOMAIN_PART_LENGTH = 255;

  private static final Pattern LOCAL_PATTERN = java.util.regex.Pattern.compile(
    ATOM + "+(\\." + ATOM + "+)*", CASE_INSENSITIVE
  );

  private static final Pattern DOMAIN_PATTERN = java.util.regex.Pattern.compile(
    DOMAIN + "|" + IP_DOMAIN, CASE_INSENSITIVE
  );

  @Override
  public void validate(String value) {
    if (StringUtils.isNotBlank(value)) {
      String[] emailParts = value.split("@");
      if (emailParts.length == 2
        && !hasTrailingDot(emailParts)
        && isValidLocalPart(emailParts[0])
        && isValidDomainPart(emailParts[1])
      ) {
        return;
      }
    }
    throw new IllegalArgumentException(String.format(INVALID_FORMAT_MESSAGE, value));
  }

  private boolean isValidDomainPart(String emailPart) {
    return matchPart(emailPart, DOMAIN_PATTERN, MAX_DOMAIN_PART_LENGTH);
  }

  private boolean isValidLocalPart(String emailPart) {
    return matchPart(emailPart, LOCAL_PATTERN, MAX_LOCAL_PART_LENGTH);
  }

  private boolean hasTrailingDot(String[] emailParts) {
    return emailParts[0].endsWith(".") || emailParts[1].endsWith(".");
  }

  private boolean matchPart(String part, Pattern pattern, int maxLength) {
    if (part.length() > maxLength) {
      return false;
    }
    Matcher matcher = pattern.matcher(part);
    return matcher.matches();
  }
}
