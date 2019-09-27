package org.folio.validate;

import org.apache.commons.lang3.Validate;
import org.folio.rest.jaxrs.model.CustomField;

public class ShortTextFieldValidator implements CustomFieldValidator {
  private static final int MAX_TEXTBOX_SHORT_LENGTH = 150;

  @Override
  public void validate(Object fieldValue, CustomField fieldDefinition) {
    Validate.isInstanceOf(String.class, fieldValue, "Text field must be a string");
    Validate.isTrue(fieldValue.toString().length() <= MAX_TEXTBOX_SHORT_LENGTH, "Maximum length of short text box field is %s", MAX_TEXTBOX_SHORT_LENGTH);
  }

  @Override
  public CustomField.Type supportedType() {
    return CustomField.Type.TEXTBOX_SHORT;
  }
}
