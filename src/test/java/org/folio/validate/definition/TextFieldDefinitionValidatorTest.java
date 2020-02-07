package org.folio.validate.definition;

import static org.junit.Assert.assertTrue;

import static org.folio.test.util.TestUtil.readJsonFile;

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

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class TextFieldDefinitionValidatorTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Autowired
  private TextFieldDefinitionValidator validator;

  @Test
  public void shouldBeApplicableForTextBoxShort() throws IOException, URISyntaxException {
    CustomField customField = readJsonFile("fields/post/textbox/postTextBoxShort.json", CustomField.class);
    assertTrue(validator.isApplicable(customField));
  }

  @Test
  public void shouldBeApplicableForTextBoxLong() throws IOException, URISyntaxException {
    CustomField customField = readJsonFile("fields/post/textbox/postTextBoxLong.json", CustomField.class);
    assertTrue(validator.isApplicable(customField));
  }

  @Test
  public void shouldReturnErrorIfContainsNotAllowedFields() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Attribute selectField is not allowed");
    CustomField customField = readJsonFile("fields/post/textbox/postWithNotAlowedFields.json", CustomField.class);
    validator.validateDefinition(customField);
  }
}
