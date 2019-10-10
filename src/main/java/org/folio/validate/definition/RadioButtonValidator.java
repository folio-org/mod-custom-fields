package org.folio.validate.definition;

import static org.folio.rest.jaxrs.model.CustomField.Type.RADIO_BUTTON;

import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class RadioButtonValidator extends SelectableField implements Validatable {

  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    validateOptions(fieldDefinition);
    validateDefaults(fieldDefinition);
    validateSingleDefaultSize(fieldDefinition);
    validateMultiSelectProperty(fieldDefinition);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return RADIO_BUTTON.equals(fieldDefinition.getType());
  }
}
