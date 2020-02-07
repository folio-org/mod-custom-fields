package org.folio.validate;

import static org.folio.validate.ValidationTestUtil.parseCustomFieldJsonValue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.spring.TestConfiguration;
import org.folio.test.util.TestUtil;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class TextFieldValidatorTest {

  @Autowired
  private TextFieldValidator validator;

  @Test
  public void shouldValidateWhenShortTextBoxValueIsCorrect() throws IOException, URISyntaxException {
    CustomField shortTextboxField = getShortTextboxFieldDefinition();
    String jsonValue = "\""  + StringUtils.repeat("*", 10) + "\"";
    validator.validate(parseCustomFieldJsonValue(jsonValue), shortTextboxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenShortTextBoxValueIsTooLong() throws IOException, URISyntaxException {
    CustomField shortTextboxField = getShortTextboxFieldDefinition();
    String jsonValue = "\""  + StringUtils.repeat("*", 16) + "\"";
    validator.validate(parseCustomFieldJsonValue(jsonValue), shortTextboxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenShortTextBoxValueIsNotString() throws IOException, URISyntaxException {
    CustomField shortTextboxField = getShortTextboxFieldDefinition();
    String jsonValue = "100";
    validator.validate(parseCustomFieldJsonValue(jsonValue), shortTextboxField);
  }

  @Test
  public void shouldValidateWhenLongTextBoxValueIsCorrect() throws IOException, URISyntaxException {
    CustomField longTextboxField = getLongTextboxFieldDefinition();
    String jsonValue = "\""  + StringUtils.repeat("*", 10) + "\"";
    validator.validate(parseCustomFieldJsonValue(jsonValue), longTextboxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenLongTextBoxValueIsTooLong() throws IOException, URISyntaxException {
    CustomField longTextboxField = getLongTextboxFieldDefinition();
    String jsonValue = "\""  + StringUtils.repeat("*", 101) + "\"";
    validator.validate(parseCustomFieldJsonValue(jsonValue), longTextboxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenLongTextBoxValueIsNotString() throws IOException, URISyntaxException {
    CustomField longTextboxField = getLongTextboxFieldDefinition();
    String jsonValue = "100";
    validator.validate(parseCustomFieldJsonValue(jsonValue), longTextboxField);
  }

  private CustomField getShortTextboxFieldDefinition() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/shortTextBoxField.json", CustomField.class);
  }

  private CustomField getLongTextboxFieldDefinition() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/longTextBoxField.json", CustomField.class);
  }
}
