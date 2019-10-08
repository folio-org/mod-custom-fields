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
public class SingleDefaultValueValidatorTest {

  @Autowired
  private SingleDefaultValueValidator validator;
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldReturnErrorIfRadioButtonIncorrectNumberOfDefaults() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The max defaults size for 'RADIO_BUTTON' custom field type is 1");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/radioButton/postRadioButtonWithInvalidDefaultsSize.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test
  public void shouldReturnErrorIfSingleSelectIncorrectNumberOfDefaults() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The max defaults size for 'SINGLE_SELECT_DROPDOWN' custom field type is 1");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postSingleSelectInvalidDefaultsSize.json", CustomField.class);
    validator.validateDefinition(customField);
  }

  @Test(expected = Test.None.class)
  public void shouldNotReturnErrorIfSingleSelectValidNumberOfDefaults() throws IOException, URISyntaxException {
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/singleSelect/postSingleSelectCustonField.json", CustomField.class);
    validator.validateDefinition(customField);
  }
}
