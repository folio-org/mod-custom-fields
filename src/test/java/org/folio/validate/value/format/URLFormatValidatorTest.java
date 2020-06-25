package org.folio.validate.value.format;

import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class URLFormatValidatorTest {

  private final URLFormatValidator validator = new URLFormatValidator();

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @DataPoints("valid-urls")
  public static String[] validUrls() {
    return new String[] {
      "http://example.com",
      "https://example.com",
      "http://new.example.com",
      "https://127.0.0.1:8080"
    };
  }

  @DataPoints("invalid-urls")
  public static String[] invalidUrls() {
    return new String[] {
      null, "",
      "://example.com",
      "url",
      "http//:example.com"
    };
  }

  @Theory
  public void testValidUrlsShouldNotThrowException(@FromDataPoints("valid-urls") String url) {
    validator.validate(url);
  }

  @Theory
  public void testInvalidUrlsShouldThrowException(@FromDataPoints("invalid-urls") String url) {
    expectedEx.expect(IllegalArgumentException.class);
    validator.validate(url);
  }

}
