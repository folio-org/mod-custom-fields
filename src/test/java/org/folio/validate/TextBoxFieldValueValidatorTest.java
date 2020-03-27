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
import org.folio.validate.value.TextBoxFieldValueValidator;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class TextBoxFieldValueValidatorTest {

  @Autowired
  private TextBoxFieldValueValidator validator;

  @Test
  public void shouldValidateWhenShortTextBoxValueIsCorrect() throws IOException, URISyntaxException {
    CustomField shortTextBoxField = getShortTextBoxFieldDefinition();
    String jsonValue = "\"" + StringUtils.repeat("*", 10) + "\"";
    validator.validate(parseCustomFieldJsonValue(jsonValue), shortTextBoxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenShortTextBoxValueIsTooLong() throws IOException, URISyntaxException {
    CustomField shortTextBoxField = getShortTextBoxFieldDefinition();
    String jsonValue = "\"" + StringUtils.repeat("*", 16) + "\"";
    validator.validate(parseCustomFieldJsonValue(jsonValue), shortTextBoxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenShortTextBoxValueIsNotString() throws IOException, URISyntaxException {
    CustomField shortTextBoxField = getShortTextBoxFieldDefinition();
    String jsonValue = "100";
    validator.validate(parseCustomFieldJsonValue(jsonValue), shortTextBoxField);
  }

  @Test
  public void shouldValidateWhenLongTextBoxValueIsCorrect() throws IOException, URISyntaxException {
    CustomField longTextBoxField = getLongTextBoxFieldDefinition();
    String jsonValue = "\"" + StringUtils.repeat("*", 10) + "\"";
    validator.validate(parseCustomFieldJsonValue(jsonValue), longTextBoxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenLongTextBoxValueIsTooLong() throws IOException, URISyntaxException {
    CustomField longTextBoxField = getLongTextBoxFieldDefinition();
    String jsonValue = "\"" + StringUtils.repeat("*", 101) + "\"";
    validator.validate(parseCustomFieldJsonValue(jsonValue), longTextBoxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenLongTextBoxValueIsNotString() throws IOException, URISyntaxException {
    CustomField longTextBoxField = getLongTextBoxFieldDefinition();
    String jsonValue = "100";
    validator.validate(parseCustomFieldJsonValue(jsonValue), longTextBoxField);
  }

  public void shouldThrowExceptionWhenShortTextBoxIsRepeatableValueIsList() throws IOException, URISyntaxException {
    CustomField shortTextBoxField = getShortTextBoxRepeatableFieldDefinition();
    String jsonValue = "[\"pizza\", \"potatoes\"]";
    validator.validate(parseCustomFieldJsonValue(jsonValue), shortTextBoxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenShortTextBoxNotRepeatableValueIsList() throws IOException, URISyntaxException {
    CustomField shortTextBoxField = getShortTextBoxFieldDefinition();
    String jsonValue = "[\"pizza\", \"potatoes\"]";
    validator.validate(parseCustomFieldJsonValue(jsonValue), shortTextBoxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenShortTextBoxIsRepeatableValueListHasNotString()
    throws IOException, URISyntaxException {
    CustomField shortTextBoxField = getShortTextBoxRepeatableFieldDefinition();
    String jsonValue = "[\"pizza\", 100]";
    validator.validate(parseCustomFieldJsonValue(jsonValue), shortTextBoxField);
  }

  private CustomField getShortTextBoxFieldDefinition() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/shortTextBoxField.json", CustomField.class);
  }

  private CustomField getShortTextBoxRepeatableFieldDefinition() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/shortTextBoxRepeatableField.json", CustomField.class);
  }

  private CustomField getLongTextBoxFieldDefinition() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/longTextBoxField.json", CustomField.class);
  }
}
