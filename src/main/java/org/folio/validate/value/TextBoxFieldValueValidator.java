package org.folio.validate.value;

import static org.apache.commons.lang3.Validate.isInstanceOf;
import static org.apache.commons.lang3.Validate.isTrue;

import static org.folio.validate.value.CustomFieldValueValidatorConstants.EXPECT_STRING_MESSAGE;
import static org.folio.validate.value.CustomFieldValueValidatorConstants.MAX_LENGTH_MESSAGE;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class TextBoxFieldValueValidator implements CustomFieldValueValidator {

  @Value("${custom.fields.value.textbox.short.length}")
  private int textBoxShortLengthLimit;

  @Value("${custom.fields.value.textbox.long.length}")
  private int textBoxLongLengthLimit;

  @Override
  public void validate(Object fieldValue, CustomField fieldDefinition) {
    boolean isRepeatable = fieldDefinition.getIsRepeatable();
    CustomField.Type type = fieldDefinition.getType();

    if (isRepeatable) {
      if (fieldValue instanceof List) {
        ((List<?>) fieldValue).forEach(val -> validateValue(val, type));
      } else {
        validateValue(fieldValue, type);
      }
    } else {
      validateValue(fieldValue, type);
    }
  }

  private void validateValue(Object fieldValue, CustomField.Type type) {
    isInstanceOf(String.class, fieldValue, EXPECT_STRING_MESSAGE, type);
    final int textFieldValueLength = fieldValue.toString().length();
    if (CustomField.Type.TEXTBOX_LONG == type) {
      isTrue(textFieldValueLength <= textBoxLongLengthLimit, MAX_LENGTH_MESSAGE, textBoxLongLengthLimit);
    } else {
      isTrue(textFieldValueLength <= textBoxShortLengthLimit, MAX_LENGTH_MESSAGE, textBoxShortLengthLimit);
    }
  }

  @Override
  public List<CustomField.Type> supportedTypes() {
    return Arrays.asList(CustomField.Type.TEXTBOX_LONG, CustomField.Type.TEXTBOX_SHORT);
  }
}
