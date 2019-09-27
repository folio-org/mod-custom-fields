package org.folio.validate;

import static org.folio.validate.ValidationTestUtil.parseCustomFieldJsonValue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.test.util.TestUtil;
import org.junit.Test;

import io.vertx.core.json.Json;

public class RadioButtonValidatorTest {
  @Test
  public void shouldValidateWhenValueIsCorrect() throws IOException, URISyntaxException {
    CustomField radioButtonField = getRadioButtonDefinition();
    String jsonValue = "\"pizza\"";
    new RadioButtonValidator().validate(parseCustomFieldJsonValue(jsonValue), radioButtonField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldReturnErrorIfValueIsNotInAllowedOptions() throws IOException, URISyntaxException {
    CustomField radioButtonField = getRadioButtonDefinition();
    String jsonValue = "\"table\"";
    new RadioButtonValidator().validate(parseCustomFieldJsonValue(jsonValue), radioButtonField);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldReturnErrorIfValueIsNotStringType() throws IOException, URISyntaxException {
    CustomField radioButtonField = getRadioButtonDefinition();
    String jsonValue = "true";
    new RadioButtonValidator().validate(parseCustomFieldJsonValue(jsonValue), radioButtonField);
  }

  private CustomField getRadioButtonDefinition() throws IOException, URISyntaxException {
    return TestUtil.readJsonFile("fields/model/radioButtonField.json", CustomField.class);
  }
}
