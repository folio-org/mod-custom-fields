package org.folio.validate.definition;

import java.io.IOException;
import java.net.URISyntaxException;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.TextField;
import org.folio.spring.TestConfiguration;
import org.folio.test.util.TestUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class CheckboxDefinitionValidatorTest {

  @Autowired
  private CheckboxDefinitionValidator validator;
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldReturnErrorIfContainsNotAllowedFields() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Attribute textField is not allowed");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/checkbox/postCheckbox.json", CustomField.class);
    customField.setTextField(new TextField());
    validator.validateDefinition(customField);
  }
}
