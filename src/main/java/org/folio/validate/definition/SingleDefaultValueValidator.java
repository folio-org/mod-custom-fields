package org.folio.validate.definition;

import static org.folio.rest.jaxrs.model.CustomField.Type.RADIO_BUTTON;
import static org.folio.rest.jaxrs.model.CustomField.Type.SINGLE_SELECT_DROPDOWN;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.SelectField;

@Component
public class SingleDefaultValueValidator implements Validatable {

  @Value("${custom.fields.definition.single.default.size}")
  private int SINGLE_DEFAULT_SIZE;

  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    validateDefaultsSize(fieldDefinition);
  }

  private void validateDefaultsSize(CustomField fieldDefinition) {

    final SelectField selectField = fieldDefinition.getSelectField();

    if (Objects.nonNull(selectField) && Objects.nonNull(selectField.getDefaults())) {

      List<String> defaults = selectField.getDefaults();
      Validate.isTrue(defaults.size() <= SINGLE_DEFAULT_SIZE,
        "The max defaults size for '" + fieldDefinition.getType() + "' custom field type is %s", SINGLE_DEFAULT_SIZE);
    }
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return RADIO_BUTTON.equals(fieldDefinition.getType()) ||
           SINGLE_SELECT_DROPDOWN.equals(fieldDefinition.getType());
  }
}
