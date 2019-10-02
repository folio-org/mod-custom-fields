package org.folio.validate.definition;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.test.util.TestUtil;

public class NameValidatorTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldReturnErrorIfIncorrectNameLength() throws IOException, URISyntaxException {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("The 'name' length cannot be more than 65");
    final CustomField customField = TestUtil.readJsonFile(
      "fields/post/postCustomNameWithTooLongName.json", CustomField.class);
    new NameValidator().validateDefinition(customField);
  }
}
