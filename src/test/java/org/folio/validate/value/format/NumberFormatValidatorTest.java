package org.folio.validate.value.format;

import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class NumberFormatValidatorTest {

  private final NumberFormatValidator validator = new NumberFormatValidator();

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @DataPoints("valid-numbers")
  public static String[] validNumbers() {
    return new String[] {
      "0", "1", "-1", "10", "123", "999", "-12443123213",
      "0.1", "0,1", ".1", ",1", "2.3", "3,4", "-1234,5678", "-1234.5678",
      "10 000 000", "10,000,000", "-10 000 000", "-10,000,000", "1 234.5671", "1 234,5672", "1,234.5673"
    };
  }

  @DataPoints("invalid-numbers")
  public static String[] invalidNumbers() {
    return new String[] {
      null, "", "qwe", "#!", "1do1", "1   1", "1,.1", "1,1,1", "1.1.1",
      "0-1", "1-", ". 1", "d,1", "10 000000", "-1320 000", "1211 234.567", "1.234.567"
    };
  }

  @Theory
  public void testValidNumbersShouldNotThrowException(@FromDataPoints("valid-numbers") String number) {
    validator.validate(number);
  }

  @Theory
  public void testInvalidNumbersShouldThrowException(@FromDataPoints("invalid-numbers") String number) {
    expectedEx.expect(IllegalArgumentException.class);
    validator.validate(number);
  }

}
