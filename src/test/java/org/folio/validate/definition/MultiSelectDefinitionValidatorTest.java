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
public class MultiSelectDefinitionValidatorTest {

  @Autowired
  private MultiSelectDefinitionValidator validator;
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldReturnErrorIfIncorrectNumberOfOptions() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The options amount should be in range 1 - 5");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/multiSelect/postWithInvalidSizeOptions.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfOptionNameTooLong() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The option value cannot be blank or have more than 65 length");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/multiSelect/postWithOptionNameTooLong.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfMultiSelectValueNotDefined() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The 'multiSelect' property should be 'true'");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/multiSelect/postWithMultiSelectValueNotDefined.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfInvalidMultiSelectValue() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The 'multiSelect' property should be 'true'");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/multiSelect/postWithInvalidMultiSelectValue.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfSelectFieldNotDefined() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The 'selectField' property should be defined");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/multiSelect/postWithSelectFieldNotDefined.json", CustomField.class);
    validator.validateDefinition(customField);
  }
}
