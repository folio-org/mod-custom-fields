package org.folio.validate.definition;

import static org.folio.validate.definition.AllowedFieldsConstants.SELECT_ALLOWED_FIELDS;

import org.folio.rest.jaxrs.model.CustomField;
import org.springframework.stereotype.Component;

@Component
public class MultiSelectDefinitionValidator implements Validatable {
  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    CustomDefinitionValidationUtil.onlyHasAllowedFields(fieldDefinition, SELECT_ALLOWED_FIELDS);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return CustomField.Type.MULTI_SELECT_DROPDOWN.equals(fieldDefinition.getType());
  }
}
