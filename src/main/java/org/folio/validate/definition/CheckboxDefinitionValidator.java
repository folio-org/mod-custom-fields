package org.folio.validate.definition;

import static org.folio.validate.definition.AllowedFieldsConstants.CHECKBOX_ALLOWED_FIELDS;

import org.folio.rest.jaxrs.model.CustomField;
import org.springframework.stereotype.Component;

@Component
public class CheckboxDefinitionValidator implements Validatable {
  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    CustomDefinitionValidationUtil.onlyHasAllowedFields(fieldDefinition, CHECKBOX_ALLOWED_FIELDS);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return CustomField.Type.SINGLE_CHECKBOX.equals(fieldDefinition.getType());
  }
}
