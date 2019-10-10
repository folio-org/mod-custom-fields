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
public class MultiSelectDropdownValidatorTest {

  @Autowired
  private MultiSelectDropdownValidator validator;
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldReturnErrorIfIncorrectNumberOfOptions() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The max option size for 'MULTI_SELECT_DROPDOWN' custom field type is 5");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/multiSelect/postWithInvalidSizeOptions.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfOptionEmpty() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The defaults size can not be more than options number");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/multiSelect/postWithDefaultsMoreThanOptions.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfInvalidDefaults() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The default value must be one of defined options: [eggs, pizza, potatoes, pie, barbeque]");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/multiSelect/postWithInvalidDefaulsValues.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfOptionNameTooLong() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The option name cannot be blank or have more than 65 characters");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/multiSelect/postWithOptionNameTooLong.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfMultiSelectValueNotDefined() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The value for 'multiSelect' should not be null.");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/multiSelect/postWithMultiSelectValueNotDefined.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfInvalidMultiSelectValue() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The value for 'multiSelect' should be 'true' for 'MULTI_SELECT_DROPDOWN' custom field type.");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/multiSelect/postWithInvalidMultiSelectValue.json", CustomField.class);
    validator.validateDefinition(customField);
  }
}
