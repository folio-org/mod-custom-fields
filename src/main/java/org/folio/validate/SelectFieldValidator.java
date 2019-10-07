package org.folio.validate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.folio.rest.jaxrs.model.CustomField;
import org.springframework.stereotype.Component;

@Component
public class SelectFieldValidator implements CustomFieldValidator {
  @Override
  public void validate(Object fieldValue, CustomField fieldDefinition) {
      Validate.isInstanceOf(String.class, fieldValue, "Field with type %s must be a string", fieldDefinition.getType());
      List<String> possibleValues = fieldDefinition.getSelectField().getOptions().getValues();
      Validate.isTrue(possibleValues.contains(fieldValue.toString()), "Field %s can only have following values %s", fieldDefinition.getRefId(), possibleValues);
  }

  @Override
  public List<CustomField.Type> supportedTypes() {
    return Arrays.asList(CustomField.Type.RADIO_BUTTON, CustomField.Type.SINGLE_SELECT_DROPDOWN);
  }
}
