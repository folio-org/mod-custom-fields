package org.folio.validate.value;

import static org.apache.commons.lang3.Validate.isInstanceOf;

import static org.folio.validate.value.CustomFieldValueValidatorConstants.EXPECT_BOOLEAN_MESSAGE;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class CheckboxFieldValueValidator implements CustomFieldValueValidator {

  @Override
  public void validate(Object fieldValue, CustomField fieldDefinition) {
    isInstanceOf(Boolean.class, fieldValue, EXPECT_BOOLEAN_MESSAGE, fieldDefinition.getType());
  }

  @Override
  public List<CustomField.Type> supportedTypes() {
    return Collections.singletonList(CustomField.Type.SINGLE_CHECKBOX);
  }
}
