package org.folio.validate.definition;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.spring.TestConfiguration;
import org.folio.test.util.TestUtil;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class OptionsValidatorTest {

  @Autowired
  private OptionsValidator validator;
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldReturnErrorIfRadioButtonIncorrectNumberOfOptions() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The max option size for 'RADIO_BUTTON' custom field type is 5");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postRadioButtonWithInvalidSizeOptions.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfSingleSelectIncorrectNumberOfOptions() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The max option size for 'SINGLE_SELECT_DROPDOWN' custom field type is 5");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postSingleSelectWithInvalidSizeOptions.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfMultiSelectIncorrectNumberOfOptions() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The max option size for 'MULTI_SELECT_DROPDOWN' custom field type is 5");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/multiSelect/postMultiSelectWithInvalidSizeOptions.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test(expected = Test.None.class)
  public void shouldNotReturnErrorIfOptionEmpty() throws IOException, URISyntaxException {
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postRadioButtonWithEmptyOptions.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test(expected = Test.None.class)
  public void shouldNotReturnErrorIfOptionNull() throws IOException, URISyntaxException {
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postRadioButtonWithNullOption.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldNotReturnErrorIfOptionValuesAreNull() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The option name cannot be blank or have more than 65 characters");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postRadioButtonWithOptionNullValues.json", CustomField.class);
    validator.validateDefinition(customField);

  }
}
