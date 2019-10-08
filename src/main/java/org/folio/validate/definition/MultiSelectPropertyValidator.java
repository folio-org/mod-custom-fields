package org.folio.validate.definition;

import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.SelectField;

@Component
public class MultiSelectPropertyValidator implements Validatable {

  @Override
  public void validateDefinition(CustomField fieldDefinition) {

    final SelectField selectField = fieldDefinition.getSelectField();

    if (Objects.nonNull(selectField) && Objects.nonNull(selectField.getMultiSelect())) {

      final Boolean multiSelect = selectField.getMultiSelect();
      final CustomField.Type customFieldType = fieldDefinition.getType();

      if(CustomField.Type.MULTI_SELECT_DROPDOWN.equals(customFieldType)){
        Validate.isTrue(multiSelect,
          "The value for 'multiSelect' should be 'true' for '" + customFieldType + "' custom field type.");
      } else {
        Validate.isTrue(!multiSelect,
          "The value for 'multiSelect' should be 'false' for '" + customFieldType + "' custom field type.");
      }
    }
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return true;
  }
}
