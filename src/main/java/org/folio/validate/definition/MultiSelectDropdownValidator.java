package org.folio.validate.definition;

import static org.folio.rest.jaxrs.model.CustomField.Type.MULTI_SELECT_DROPDOWN;

import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class MultiSelectDropdownValidator extends SelectableField implements Validatable {

  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    validateDefaults(fieldDefinition);
    validateOptions(fieldDefinition);
    validateMultiSelectProperty(fieldDefinition);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return MULTI_SELECT_DROPDOWN.equals(fieldDefinition.getType());
  }
}
