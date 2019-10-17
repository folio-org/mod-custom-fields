package org.folio.validate.definition;

import static org.folio.rest.jaxrs.model.CustomField.Type.SINGLE_SELECT_DROPDOWN;
import static org.folio.validate.definition.AllowedFieldsConstants.SELECT_ALLOWED_FIELDS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class SingleSelectDropdownValidator extends SelectableField implements Validatable {

  @Value("${custom.fields.definition.dropdown.option.size.max}")
  private int SINGLE_SELECT_OPTION_SIZE_MAX;

  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    CustomDefinitionValidationUtil.onlyHasAllowedFields(fieldDefinition, SELECT_ALLOWED_FIELDS);
    validateSelectFieldDefined(fieldDefinition);
    validateDefaults(fieldDefinition);
    validateSingleDefaultSize(fieldDefinition);
    validateOptions(fieldDefinition, SINGLE_SELECT_OPTION_SIZE_MAX);
    validateMultiSelectProperty(fieldDefinition, false);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return SINGLE_SELECT_DROPDOWN.equals(fieldDefinition.getType());
  }
}
