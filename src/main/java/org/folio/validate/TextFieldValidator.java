package org.folio.validate;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class TextFieldValidator implements CustomFieldValidator {

  private static final String MAXIMUM_LENGTH_MESSAGE = "Maximum length of this text field is %s";

  @Value("${custom.fields.value.textbox.short.length}")
  private int TEXT_BOX_SHORT_LENGTH;

  @Value("${custom.fields.value.textbox.long.length}")
  private int TEXT_BOX_LONG_LENGTH;

  @Override
  public void validate(Object fieldValue, CustomField fieldDefinition) {
    Validate.isInstanceOf(String.class, fieldValue, "Text field must be a string");
    final int textFieldValueLength = fieldValue.toString().length();
    if(CustomField.Type.TEXTBOX_LONG.equals(fieldDefinition.getType())){
      Validate.isTrue(textFieldValueLength <= TEXT_BOX_LONG_LENGTH, MAXIMUM_LENGTH_MESSAGE, TEXT_BOX_LONG_LENGTH);
    } else {
      Validate.isTrue(textFieldValueLength <= TEXT_BOX_SHORT_LENGTH, MAXIMUM_LENGTH_MESSAGE, TEXT_BOX_SHORT_LENGTH);
    }
  }

  @Override
  public List<CustomField.Type> supportedTypes() {
    return Arrays.asList(CustomField.Type.TEXTBOX_LONG, CustomField.Type.TEXTBOX_SHORT);
  }
}
