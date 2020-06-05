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
public class RadioButtonDefinitionValidatorTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();
  @Autowired
  private RadioButtonDefinitionValidator validator;

  @Test
  public void shouldReturnErrorIfIncorrectNumberOfOptions() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The options amount should be in range 1 - 5");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithInvalidSizeOptions.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfOptionEmpty() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The options amount should be in range 1 - 5");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithEmptyOptions.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfOptionValuesAreNull() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Option should not be null");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithOptionNullValues.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfIncorrectNumberOfDefaults() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The max defaults amount is 1");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithInvalidDefaultsAmount.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfMultiSelectValueNotDefined() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The 'multiSelect' property should be 'false'");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithMultiSelectValueNotDefined.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfInvalidMultiSelectValue() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The 'multiSelect' property should be 'false'");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithInvalidMultiSelectValue.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfSelectFieldNotDefined() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The 'selectField' property should be defined");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithSelectFieldNotDefined.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldNotReturnErrorIfDefaultValueIsEmpty() throws IOException, URISyntaxException {
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postWithDefaultsEmpty.json", CustomField.class);
    validator.validateDefinition(customField);
  }
}
