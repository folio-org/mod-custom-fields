package org.folio.validate.definition;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.test.util.TestUtil;

public class HelpTextValidatorTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldReturnErrorIfIncorrectHelpTextLength() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The 'helpText' length cannot be more than 100");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/postCustomFieldHelpTextInvalid.json", CustomField.class);
    new HelpTextValidator().validateDefinition(customField);
  }
}
