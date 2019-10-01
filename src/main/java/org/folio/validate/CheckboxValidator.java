package org.folio.validate;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.folio.rest.jaxrs.model.CustomField;

public class CheckboxValidator implements CustomFieldValidator {
  @Override
  public void validate(Object fieldValue, CustomField fieldDefinition) {
    Validate.isInstanceOf(Boolean.class, fieldValue, "Checkbox must have a boolean value");
  }

  @Override
  public List<CustomField.Type> supportedTypes() {
    return Collections.singletonList(CustomField.Type.SINGLE_CHECKBOX);
  }
}
