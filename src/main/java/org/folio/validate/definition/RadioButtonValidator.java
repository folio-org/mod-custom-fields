package org.folio.validate.definition;

import static org.folio.rest.jaxrs.model.CustomField.Type.RADIO_BUTTON;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class RadioButtonValidator extends SelectableField implements Validatable {

  @Value("${custom.fields.definition.radio.button.option.size.max}")
  private int RADIO_BUTTON_OPTION_SIZE_MAX;

  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    validateSelectFieldDefined(fieldDefinition);
    validateDefaults(fieldDefinition);
    validateSingleDefaultSize(fieldDefinition);
    validateOptions(fieldDefinition, RADIO_BUTTON_OPTION_SIZE_MAX);
    validateMultiSelectProperty(fieldDefinition, false);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return RADIO_BUTTON.equals(fieldDefinition.getType());
  }
}
