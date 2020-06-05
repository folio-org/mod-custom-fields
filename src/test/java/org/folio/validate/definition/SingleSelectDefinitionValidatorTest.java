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
public class SingleSelectDefinitionValidatorTest {

  @Autowired
  private SingleSelectDefinitionValidator validator;
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldReturnErrorIfSingleSelectIncorrectNumberOfOptions() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The options amount should be in range 1 - 5");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithInvalidOptionsSize.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfSingleSelectIncorrectNumberOfDefaults() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The max defaults amount is 1");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithInvalidDefaultsSize.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfOptionNameTooLong() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The option value cannot be blank or have more than 65 length");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithOptionNameTooLong.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfMultiSelectValueNotDefined() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The 'multiSelect' property should be 'false'");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithMultiSelectValueNotDefined.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfInvalidMultiSelectValue() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The 'multiSelect' property should be 'false'");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithInvalidMultiSelectValue.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfSelectFieldNotDefined() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The 'selectField' property should be defined");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithSelectFieldNotDefined.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfIdsAreNotUnique() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Option IDs should be unique");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postWithNotUniqueIds.json", CustomField.class);
    validator.validateDefinition(customField);
  }
}
