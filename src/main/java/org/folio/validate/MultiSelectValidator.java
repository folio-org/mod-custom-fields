package org.folio.validate;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.folio.rest.jaxrs.model.CustomField;

public class MultiSelectValidator implements CustomFieldValidator{

  @Override
  @SuppressWarnings("unchecked")
  public void validate(Object fieldValue, CustomField fieldDefinition) {
    Validate.isInstanceOf(List.class, fieldValue, "Field with type %s must be an array", fieldDefinition.getType());

    List listValue = (List) fieldValue;
    boolean allStrings = listValue.stream()
      .allMatch(value -> value instanceof String);
    Validate.isTrue(allStrings, "All values of type %s must have type String", fieldDefinition.getType());

    List<String> stringList = (List<String>) listValue;
    List<String> possibleValues = fieldDefinition.getSelectField().getOptions().getValues();
    boolean allValuesValid = possibleValues.containsAll(stringList);
    Validate.isTrue(allValuesValid, "Field %s can only have following values %s", fieldDefinition.getRefId(), possibleValues);
  }

  @Override
  public List<CustomField.Type> supportedTypes() {
    return Collections.singletonList(CustomField.Type.MULTI_SELECT_DROPDOWN);
  }
}
