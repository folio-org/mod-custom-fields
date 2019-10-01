package org.folio.validate;

import static org.folio.validate.ValidationTestUtil.parseCustomFieldJsonValue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.folio.rest.jaxrs.model.CustomField;
import org.folio.test.util.TestUtil;
import org.junit.Test;

public class TextFieldValidatorTest {
  @Test
  public void shouldValidateWhenValueIsCorrect() throws IOException, URISyntaxException {
    CustomField textboxField = getTextboxFieldDefinition();
    String jsonValue = "\""  + StringUtils.repeat("*", textboxField.getTextField().getMaxSize()) + "\"";
    new TextFieldValidator().validate(parseCustomFieldJsonValue(jsonValue), textboxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenValueIsTooLong() throws IOException, URISyntaxException {
    CustomField textboxField = getTextboxFieldDefinition();
    String jsonValue = "\""  + StringUtils.repeat("*", textboxField.getTextField().getMaxSize() + 1) + "\"";
    new TextFieldValidator().validate(parseCustomFieldJsonValue(jsonValue), textboxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenValueIsNotString() throws IOException, URISyntaxException {
    CustomField textboxField = getTextboxFieldDefinition();
    String jsonValue = "100";
    new TextFieldValidator().validate(parseCustomFieldJsonValue(jsonValue), textboxField);
  }

  private CustomField getTextboxFieldDefinition() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/shortTextBoxField.json", CustomField.class);
  }
}
