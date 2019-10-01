package org.folio.validate;

import static org.folio.validate.ValidationTestUtil.parseCustomFieldJsonValue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.test.util.TestUtil;
import org.junit.Test;

public class CheckboxValidatorTest {

  @Test
  public void shouldValidateWhenValueIsCorrect() throws IOException, URISyntaxException {
    CustomField checkboxField = getCheckboxDefinition();
    String jsonValue = "true";
    new CheckboxValidator().validate(parseCustomFieldJsonValue(jsonValue), checkboxField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldReturnErrorIfValueIsNotBooleanType() throws IOException, URISyntaxException {
    CustomField checkboxField = getCheckboxDefinition();
    String jsonValue = "\"true\"";
    new CheckboxValidator().validate(parseCustomFieldJsonValue(jsonValue), checkboxField);
  }

  private CustomField getCheckboxDefinition() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/checkboxField.json", CustomField.class);
  }
}
