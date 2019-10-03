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
public class DefaultsValidatorTest {

  @Autowired
  private DefaultsValidator validator;
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldReturnErrorIfNumberOfDefaultsMoreThanDefined() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The defaults size can not be more than options number");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postRadioButtonWithDefaultSizeMoreThanDefined.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfIncorrectDefaultValue() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The default value must be one of defined options: [eggs, pizza, potatoes, pie, barbeque]");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postRadioButtonWithInvalidDefaultValues.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test(expected = Test.None.class)
  public void shouldNotReturnErrorIfDefaultIsNull() throws IOException, URISyntaxException {
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postRadioButtonWithDefaultIsNull.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test(expected = Test.None.class)
  public void shouldNotReturnErrorIfDefaultValueIsEmpty() throws IOException, URISyntaxException {
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postRadioButtonWithDefaultsEmpty.json", CustomField.class);
    validator.validateDefinition(customField);
  }
}
