package org.folio.validate.definition;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class OptionSizeValidator implements Validatable {

  private static final int RADIO_BUTTON_MAX_OPTION_SIZE = 5;

  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    Validate.isTrue(fieldDefinition.getSelectField().getOptions().getValues().size() <= RADIO_BUTTON_MAX_OPTION_SIZE,
      "The max option size for 'Radio button' custom field type is %s", RADIO_BUTTON_MAX_OPTION_SIZE);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return CustomField.Type.RADIO_BUTTON.equals(fieldDefinition.getType());
  }
}
