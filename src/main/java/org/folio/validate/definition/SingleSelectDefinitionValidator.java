package org.folio.validate.definition;

import static org.folio.rest.jaxrs.model.CustomField.Type.SINGLE_SELECT_DROPDOWN;
import static org.folio.validate.definition.AllowedFieldsConstants.SELECT_ALLOWED_FIELDS;
import static org.folio.validate.definition.CustomDefinitionValidationUtil.onlyHasAllowedFields;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class SingleSelectDefinitionValidator extends SelectableField implements Validatable {

  @Value("${custom.fields.definition.dropdown.option.size.max}")
  private int singleSelectOptionsMaxSize;

  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    onlyHasAllowedFields(fieldDefinition, SELECT_ALLOWED_FIELDS);
    validateSelectFieldDefined(fieldDefinition);
    validateDefaults(fieldDefinition);
    validateSingleDefaultSize(fieldDefinition);
    validateOptions(fieldDefinition, singleSelectOptionsMaxSize);
    validateMultiSelectProperty(fieldDefinition, false);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return SINGLE_SELECT_DROPDOWN.equals(fieldDefinition.getType());
  }
}
