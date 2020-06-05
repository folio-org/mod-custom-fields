package org.folio.validate;

import static org.folio.validate.ValidationTestUtil.parseCustomFieldJsonValue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.test.util.TestUtil;
import org.folio.validate.value.SelectFieldValueValidator;

public class SelectFieldValueValidatorTest {

  private final SelectFieldValueValidator validator = new SelectFieldValueValidator();

  @Test
  public void shouldValidateWhenAllArrayValuesAreCorrect() throws IOException, URISyntaxException {
    CustomField fieldDefinition = getMultiSelectDropdownField();
    String jsonValue = "[\"pizza\", \"potatoes\"]";
    validator.validate(parseCustomFieldJsonValue(jsonValue), fieldDefinition);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfOneValueIsNotInAllowedOptions() throws IOException, URISyntaxException {
    CustomField fieldDefinition = getMultiSelectDropdownField();
    String jsonValue = "[\"pizza\", \"table\",\"eggs\"]";
    validator.validate(parseCustomFieldJsonValue(jsonValue), fieldDefinition);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfOneValueIsNotAString() throws IOException, URISyntaxException {
    CustomField fieldDefinition = getMultiSelectDropdownField();
    String jsonValue = "[\"pizza\", {\"eggs\" : \"eggs\"},\"eggs\"]";
    validator.validate(parseCustomFieldJsonValue(jsonValue), fieldDefinition);
  }

  @Test
  public void shouldValidateWhenValueIsCorrect() throws IOException, URISyntaxException {
    CustomField radioButtonField = getRadioButtonField();
    String jsonValue = "\"pizza\"";
    validator.validate(parseCustomFieldJsonValue(jsonValue), radioButtonField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfValueIsNotInAllowedOptions() throws IOException, URISyntaxException {
    CustomField radioButtonField = getRadioButtonField();
    String jsonValue = "\"table\"";
    validator.validate(parseCustomFieldJsonValue(jsonValue), radioButtonField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfValueIsNotStringType() throws IOException, URISyntaxException {
    CustomField radioButtonField = getRadioButtonField();
    String jsonValue = "true";
    validator.validate(parseCustomFieldJsonValue(jsonValue), radioButtonField);
  }

  @Test
  public void shouldValidateDropdownWhenValueIsCorrect() throws IOException, URISyntaxException {
    CustomField dropdownField = getSingleSelectDropdownField();
    String jsonValue = "\"pizza\"";
    validator.validate(parseCustomFieldJsonValue(jsonValue), dropdownField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfDropdownValueIsNotInAllowedOptions() throws IOException, URISyntaxException {
    CustomField dropdownField = getSingleSelectDropdownField();
    String jsonValue = "\"table\"";
    validator.validate(parseCustomFieldJsonValue(jsonValue), dropdownField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfDropdownValueIsNotStringType() throws IOException, URISyntaxException {
    CustomField dropdownField = getSingleSelectDropdownField();
    String jsonValue = "true";
    validator.validate(parseCustomFieldJsonValue(jsonValue), dropdownField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfDropdownValueIsArray() throws IOException, URISyntaxException {
    CustomField dropdownField = getSingleSelectDropdownField();
    String jsonValue = "[\"pizza\", \"potatoes\"]";
    validator.validate(parseCustomFieldJsonValue(jsonValue), dropdownField);
  }

  @Test
  public void shouldValidateIfRepeatableFieldArrayValuesAreCorrect() throws IOException, URISyntaxException {
    CustomField fieldDefinition = getSingleSelectRepeatableField();
    String jsonValue = "[\"pizza\", \"potatoes\"]";
    validator.validate(parseCustomFieldJsonValue(jsonValue), fieldDefinition);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfRepeatableArrayValueIsNotStringType() throws IOException, URISyntaxException {
    CustomField fieldDefinition = getSingleSelectDropdownField();
    String jsonValue = "[\"pizza\", 100]";
    validator.validate(parseCustomFieldJsonValue(jsonValue), fieldDefinition);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfRepeatableArrayValueIsNotInAllowedOptions() throws IOException, URISyntaxException {
    CustomField fieldDefinition = getSingleSelectDropdownField();
    String jsonValue = "[\"pizza\", \"table\"]";
    validator.validate(parseCustomFieldJsonValue(jsonValue), fieldDefinition);
  }

  private CustomField getRadioButtonField() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/radioButtonField.json", CustomField.class);
  }

  private CustomField getSingleSelectDropdownField() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/singleSelectField.json", CustomField.class);
  }

  private CustomField getSingleSelectRepeatableField() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/singleSelectRepeatableField.json", CustomField.class);
  }

  private CustomField getMultiSelectDropdownField() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/multiSelectField.json", CustomField.class);
  }
}
