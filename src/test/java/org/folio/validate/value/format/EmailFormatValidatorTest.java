package org.folio.validate.value.format;

import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class EmailFormatValidatorTest {

  private final EmailFormatValidator validator = new EmailFormatValidator();

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @DataPoints("valid-emails")
  public static String[] validEmails() {
    return new String[] {
      "email@example.com",
      "e@example.com",
      "!#$%&'*+-/=?^_`.{|}~@example.com",
      "email@example-new.com"
    };
  }

  @DataPoints("invalid-emails")
  public static String[] invalidEmails() {
    return new String[] {
      null, "",
      "@",
      "@a.com",
      "email@",
      "email@.com",
      "email@example",
      "email",
      "em@il@ex@mle.com",
      StringUtils.repeat('a', 65) + "@example.com",
      "email@" + StringUtils.repeat('a', 256)
    };
  }

  @Theory
  public void testValidEmailsShouldNotThrowException(@FromDataPoints("valid-emails") String email) {
    validator.validate(email);
  }

  @Theory
  public void testInvalidEmailsShouldThrowException(@FromDataPoints("invalid-emails") String email) {
    expectedEx.expect(IllegalArgumentException.class);
    validator.validate(email);
  }

}
