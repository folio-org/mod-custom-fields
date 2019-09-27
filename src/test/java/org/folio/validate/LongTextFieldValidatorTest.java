package org.folio.validate;

import static org.folio.validate.ValidationTestUtil.parseCustomFieldJsonValue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.folio.rest.jaxrs.model.CustomField;
import org.folio.test.util.TestUtil;
import org.junit.Test;

public class LongTextFieldValidatorTest {
  @Test
  public void shouldValidateWhenValueIsCorrect() throws IOException, URISyntaxException {
    CustomField textboxField = TestUtil.readJsonFile("fields/model/longTextBoxField.json", CustomField.class);
    String jsonValue = "\""  + StringUtils.repeat("*", 1500) + "\"";
    new LongTextFieldValidator().validate(parseCustomFieldJsonValue(jsonValue), textboxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenValueIsTooLong() throws IOException, URISyntaxException {
    CustomField textboxField = TestUtil.readJsonFile("fields/model/longTextBoxField.json", CustomField.class);
    String jsonValue = "\""  + StringUtils.repeat("*", 1501) + "\"";
    new LongTextFieldValidator().validate(parseCustomFieldJsonValue(jsonValue), textboxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenValueIsNotString() throws IOException, URISyntaxException {
    CustomField textboxField = TestUtil.readJsonFile("fields/model/longTextBoxField.json", CustomField.class);
    String jsonValue = "100";
    new LongTextFieldValidator().validate(parseCustomFieldJsonValue(jsonValue), textboxField);
  }
}
