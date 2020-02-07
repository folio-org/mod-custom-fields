package org.folio.validate.definition;

import static org.folio.rest.jaxrs.model.CustomField.Type.MULTI_SELECT_DROPDOWN;
import static org.folio.validate.definition.AllowedFieldsConstants.SELECT_ALLOWED_FIELDS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class MultiSelectDropdownValidator extends SelectableField implements Validatable {

  @Value("${custom.fields.definition.dropdown.option.size.max}")
  private int multiSelectOptionsMaxSize;

  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    CustomDefinitionValidationUtil.onlyHasAllowedFields(fieldDefinition, SELECT_ALLOWED_FIELDS);
    validateSelectFieldDefined(fieldDefinition);
    validateDefaults(fieldDefinition);
    validateOptions(fieldDefinition, multiSelectOptionsMaxSize);
    validateMultiSelectProperty(fieldDefinition, true);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return MULTI_SELECT_DROPDOWN.equals(fieldDefinition.getType());
  }
}
