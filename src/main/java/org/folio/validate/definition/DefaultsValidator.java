package org.folio.validate.definition;

import static org.folio.rest.jaxrs.model.CustomField.Type.MULTI_SELECT_DROPDOWN;
import static org.folio.rest.jaxrs.model.CustomField.Type.RADIO_BUTTON;
import static org.folio.rest.jaxrs.model.CustomField.Type.SINGLE_SELECT_DROPDOWN;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.SelectField;

@Component
public class DefaultsValidator implements Validatable {

  @Override
  public void validateDefinition(CustomField fieldDefinition) {

    final SelectField selectField = fieldDefinition.getSelectField();

    if (Objects.nonNull(selectField) && Objects.nonNull(selectField.getDefaults())) {

      List<String> defaults = selectField.getDefaults();

      if (!defaults.isEmpty()) {
        final List<String> values = fieldDefinition.getSelectField().getOptions().getValues();

        Validate.isTrue(values.size() >= defaults.size(), "The defaults size can not be more than options number");
        Validate.isTrue(values.containsAll(defaults), "The default value must be one of defined options: %s", values);
      }
    }
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return RADIO_BUTTON.equals(fieldDefinition.getType()) ||
           SINGLE_SELECT_DROPDOWN.equals(fieldDefinition.getType()) ||
           MULTI_SELECT_DROPDOWN.equals(fieldDefinition.getType());
  }
}
