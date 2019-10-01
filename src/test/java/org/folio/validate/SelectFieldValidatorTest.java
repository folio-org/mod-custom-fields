package org.folio.validate;

import static org.folio.validate.ValidationTestUtil.parseCustomFieldJsonValue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.test.util.TestUtil;
import org.junit.Test;

public class SelectFieldValidatorTest {
  @Test
  public void shouldValidateWhenValueIsCorrect() throws IOException, URISyntaxException {
    CustomField radioButtonField = getRadioButtonDefinition();
    String jsonValue = "\"pizza\"";
    new SelectFieldValidator().validate(parseCustomFieldJsonValue(jsonValue), radioButtonField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfValueIsNotInAllowedOptions() throws IOException, URISyntaxException {
    CustomField radioButtonField = getRadioButtonDefinition();
    String jsonValue = "\"table\"";
    new SelectFieldValidator().validate(parseCustomFieldJsonValue(jsonValue), radioButtonField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfValueIsNotStringType() throws IOException, URISyntaxException {
    CustomField radioButtonField = getRadioButtonDefinition();
    String jsonValue = "true";
    new SelectFieldValidator().validate(parseCustomFieldJsonValue(jsonValue), radioButtonField);
  }

  @Test
  public void shouldValidateDropdownWhenValueIsCorrect() throws IOException, URISyntaxException {
    CustomField dropdownField = getSingleSelectDropdownDefinition();
    String jsonValue = "\"pizza\"";
    new SelectFieldValidator().validate(parseCustomFieldJsonValue(jsonValue), dropdownField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfDropdownValueIsNotInAllowedOptions() throws IOException, URISyntaxException {
    CustomField dropdownField = getSingleSelectDropdownDefinition();
    String jsonValue = "\"table\"";
    new SelectFieldValidator().validate(parseCustomFieldJsonValue(jsonValue), dropdownField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfDropdownValueIsNotStringType() throws IOException, URISyntaxException {
    CustomField dropdownField = getSingleSelectDropdownDefinition();
    String jsonValue = "true";
    new SelectFieldValidator().validate(parseCustomFieldJsonValue(jsonValue), dropdownField);
  }

  private CustomField getRadioButtonDefinition() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/radioButtonField.json", CustomField.class);
  }

  private CustomField getSingleSelectDropdownDefinition() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/singleSelectDropdown.json", CustomField.class);
  }
}
