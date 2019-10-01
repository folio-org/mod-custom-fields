package org.folio.validate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.folio.rest.jaxrs.model.CustomField;

public class TextFieldValidator implements CustomFieldValidator {
  @Override
  public void validate(Object fieldValue, CustomField fieldDefinition) {
    Validate.isInstanceOf(String.class, fieldValue, "Text field must be a string");
    Integer maxSize = fieldDefinition.getTextField().getMaxSize();
    Validate.isTrue(fieldValue.toString().length() <= maxSize, "Maximum length of short text box field is %s" , maxSize);
  }

  @Override
  public List<CustomField.Type> supportedTypes() {
    return Arrays.asList(CustomField.Type.TEXTBOX_LONG, CustomField.Type.TEXTBOX_SHORT);
  }
}
