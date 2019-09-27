package org.folio.validate;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.folio.rest.jaxrs.model.CustomField;
import org.springframework.stereotype.Component;

@Component
public class RadioButtonValidator implements CustomFieldValidator {
  @Override
  public void validate(Object fieldValue, CustomField fieldDefinition) {
      Validate.isInstanceOf(String.class, fieldValue, "Radio button must be a string");
      List<String> possibleValues = fieldDefinition.getSelectField().getOptions().getValues();
      Validate.isTrue(possibleValues.contains(fieldValue.toString()), "Field %s can only have following values %s", fieldDefinition.getRefId(), possibleValues);
  }

  @Override
  public CustomField.Type supportedType() {
    return CustomField.Type.RADIO_BUTTON;
  }
}
