package org.folio.validate;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.test.util.TestUtil;
import org.folio.validate.definition.RadioButtonDefinitionValidator;

public class RadioButtonDefinitionValidatorTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldReturnErrorIfIncorrectNumberOfOptions() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The max option size for 'Radio button' custom field type is 5");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postRadioButtonWithInvalidSizeOptions.json", CustomField.class);
    new RadioButtonDefinitionValidator().validateDefinition(customField);
  }
}
