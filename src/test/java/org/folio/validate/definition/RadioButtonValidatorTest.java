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
public class RadioButtonValidatorTest {

  @Autowired
  private RadioButtonValidator validator;
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldReturnErrorIfIncorrectNumberOfOptions() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The max option size for 'RADIO_BUTTON' custom field type is 5");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithInvalidSizeOptions.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfOptionEmpty() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The defaults size can not be more than options number");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithEmptyOptions.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfNumberOfDefaultsMoreThanDefined() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The defaults size can not be more than options number");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithDefaultSizeMoreThanDefined.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfOptionNull() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Options can not be null if 'defaults' defined.");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithNullOption.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfOptionValuesAreNull() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The option name cannot be blank or have more than 65 characters");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithOptionNullValues.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfIncorrectNumberOfDefaults() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The max defaults size for 'RADIO_BUTTON' custom field type is 1");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithInvalidDefaultsSize.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfIncorrectDefaultValue() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The default value must be one of defined options: [eggs, pizza, potatoes, pie, barbeque]");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithInvalidDefaultValues.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfMultiSelectValueNotDefined() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The value for 'multiSelect' should not be null.");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithMultiSelectValueNotDefined.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfInvalidMultiSelectValue() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The value for 'multiSelect' should be 'false' for 'RADIO_BUTTON' custom field type.");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithInvalidMultiSelectValue.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test(expected = Test.None.class)
  public void shouldNotReturnErrorIfDefaultIsNull() throws IOException, URISyntaxException {
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithDefaultIsNull.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test(expected = Test.None.class)
  public void shouldNotReturnErrorIfDefaultValueIsEmpty() throws IOException, URISyntaxException {
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithDefaultsEmpty.json", CustomField.class);
    validator.validateDefinition(customField);
  }
}
