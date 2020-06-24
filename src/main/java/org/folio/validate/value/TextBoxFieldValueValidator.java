package org.folio.validate.value;

import static org.apache.commons.lang3.Validate.isInstanceOf;
import static org.apache.commons.lang3.Validate.isTrue;

import static org.folio.validate.value.CustomFieldValueValidatorConstants.EXPECT_STRING_MESSAGE;
import static org.folio.validate.value.CustomFieldValueValidatorConstants.MAX_LENGTH_MESSAGE;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.TextField;
import org.folio.validate.value.format.EmailFormatValidator;
import org.folio.validate.value.format.FormatValidator;
import org.folio.validate.value.format.NumberFormatValidator;
import org.folio.validate.value.format.TextFormatValidator;
import org.folio.validate.value.format.URLFormatValidator;

@Component
public class TextBoxFieldValueValidator implements CustomFieldValueValidator {

  @Value("${custom.fields.value.textbox.short.length}")
  private int textBoxShortLengthLimit;

  @Value("${custom.fields.value.textbox.long.length}")
  private int textBoxLongLengthLimit;

  private final Map<TextField.FieldFormat, FormatValidator> formatValidators;

  {
    formatValidators = new HashedMap<>();
    formatValidators.put(TextField.FieldFormat.TEXT, new TextFormatValidator());
    formatValidators.put(TextField.FieldFormat.EMAIL, new EmailFormatValidator());
    formatValidators.put(TextField.FieldFormat.NUMBER, new NumberFormatValidator());
    formatValidators.put(TextField.FieldFormat.URL, new URLFormatValidator());
  }

  @Override
  public void validate(Object fieldValue, CustomField fieldDefinition) {
    boolean isRepeatable = fieldDefinition.getIsRepeatable();

    if (isRepeatable) {
      if (fieldValue instanceof List) {
        ((List<?>) fieldValue).forEach(val -> validateValue(val, fieldDefinition));
      } else {
        validateValue(fieldValue, fieldDefinition);
      }
    } else {
      validateValue(fieldValue, fieldDefinition);
    }
  }

  @Override
  public List<CustomField.Type> supportedTypes() {
    return Arrays.asList(CustomField.Type.TEXTBOX_LONG, CustomField.Type.TEXTBOX_SHORT);
  }

  private void validateValue(Object fieldValue, CustomField fieldDefinition) {
    CustomField.Type type = fieldDefinition.getType();
    TextField.FieldFormat fieldFormat = fieldDefinition.getTextField().getFieldFormat();
    isInstanceOf(String.class, fieldValue, EXPECT_STRING_MESSAGE, type);
    formatValidators.get(fieldFormat).validate(fieldValue.toString());
    final int textFieldValueLength = fieldValue.toString().length();
    if (CustomField.Type.TEXTBOX_LONG == type) {
      isTrue(textFieldValueLength <= textBoxLongLengthLimit, MAX_LENGTH_MESSAGE, textBoxLongLengthLimit);
    } else {
      isTrue(textFieldValueLength <= textBoxShortLengthLimit, MAX_LENGTH_MESSAGE, textBoxShortLengthLimit);
    }
  }
}
