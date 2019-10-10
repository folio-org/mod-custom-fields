package org.folio.validate.definition;

import static org.folio.rest.jaxrs.model.CustomField.Type.SINGLE_SELECT_DROPDOWN;

import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class SingleSelectDropdownValidator extends SelectableField implements Validatable {

  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    validateDefaults(fieldDefinition);
    validateSingleDefaultSize(fieldDefinition);
    validateOptions(fieldDefinition);
    validateMultiSelectProperty(fieldDefinition);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return SINGLE_SELECT_DROPDOWN.equals(fieldDefinition.getType());
  }
}
