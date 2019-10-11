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
public class SingleSelectDropdownValidatorTest {

  @Autowired
  private SingleSelectDropdownValidator validator;
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldReturnErrorIfSingleSelectIncorrectNumberOfOptions() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The max option size for 'SINGLE_SELECT_DROPDOWN' custom field type is 5");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithInvalidOptionsSize.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfSingleSelectIncorrectNumberOfDefaults() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The max defaults size for 'SINGLE_SELECT_DROPDOWN' custom field type is 1");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postInvalidDefaultsSize.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfOptionEmpty() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The default value must be one of defined options: [potatoes]");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithDefaultsMoreThanOptions.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfInvalidDefaults() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The default value must be one of defined options: [eggs, pizza, potatoes, pie, barbeque]");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithInvalidDefaulsValues.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfOptionNameTooLong() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The option name cannot be blank or have more than 65 characters");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithOptionNameTooLong.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfMultiSelectValueNotDefined() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The value for 'multiSelect' should not be null.");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithMultiSelectValueNotDefined.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfInvalidMultiSelectValue() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The value for 'multiSelect' should be 'false' for 'SINGLE_SELECT_DROPDOWN' custom field type.");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithInvalidMultiSelectValue.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfSelectFieldNotDefined() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The 'selectField' property should be defined for 'SINGLE_SELECT_DROPDOWN' custom field type.");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithSelectFieldNotDefined.json", CustomField.class);
    validator.validateDefinition(customField);
  }
}
