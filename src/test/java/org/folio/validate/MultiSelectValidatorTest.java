package org.folio.validate;

import static org.folio.validate.ValidationTestUtil.parseCustomFieldJsonValue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.test.util.TestUtil;
import org.junit.Test;

public class MultiSelectValidatorTest {
  @Test
  public void shouldValidateWhenAllArrayValuesAreCorrect() throws IOException, URISyntaxException {
    CustomField fieldDefinition = getMultiSelectDropdownDefinition();
    String jsonValue = "[\"pizza\", \"potatoes\"]";
    new MultiSelectValidator().validate(parseCustomFieldJsonValue(jsonValue), fieldDefinition);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfOneValueIsNotInAllowedOptions() throws IOException, URISyntaxException {
    CustomField fieldDefinition = getMultiSelectDropdownDefinition();
    String jsonValue = "[\"pizza\", \"table\",\"eggs\"]";
    new MultiSelectValidator().validate(parseCustomFieldJsonValue(jsonValue), fieldDefinition);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfValueIsNotArrayType() throws IOException, URISyntaxException {
    CustomField fieldDefinition = getMultiSelectDropdownDefinition();
    String jsonValue = "\"pizza\"";
    new MultiSelectValidator().validate(parseCustomFieldJsonValue(jsonValue), fieldDefinition);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfOneValueIsNotAString() throws IOException, URISyntaxException {
    CustomField fieldDefinition = getMultiSelectDropdownDefinition();
    String jsonValue = "[\"pizza\", {\"eggs\" : \"eggs\"},\"eggs\"]";
    new MultiSelectValidator().validate(parseCustomFieldJsonValue(jsonValue), fieldDefinition);
  }

  private CustomField getMultiSelectDropdownDefinition() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/multiSelectDropdown.json", CustomField.class);
  }
}
