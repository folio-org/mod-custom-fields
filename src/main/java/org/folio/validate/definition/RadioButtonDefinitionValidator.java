package org.folio.validate.definition;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.SelectField;

@Component
public class RadioButtonDefinitionValidator implements Validatable {

  @Value("${custom.fields.definition.radioButton.option.default.size}")
  private int RADIO_BUTTON_DEFAULTS_SIZE;

  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    validateDefaultsSize(fieldDefinition);
  }

  private void validateDefaultsSize(CustomField fieldDefinition) {

    final SelectField selectField = fieldDefinition.getSelectField();

    if (Objects.nonNull(selectField) && Objects.nonNull(selectField.getDefaults())) {

      List<String> defaults = selectField.getDefaults();
      Validate.isTrue(defaults.size() <= RADIO_BUTTON_DEFAULTS_SIZE,
        "The max defaults size for '" + fieldDefinition.getType() + "' custom field type is %s", RADIO_BUTTON_DEFAULTS_SIZE);
    }
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return CustomField.Type.RADIO_BUTTON.equals(fieldDefinition.getType());
  }
}
