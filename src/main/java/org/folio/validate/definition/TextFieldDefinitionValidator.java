package org.folio.validate.definition;

import static org.folio.validate.definition.AllowedFieldsConstants.TEXTBOX_ALLOWED_FIELDS;

import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class TextFieldDefinitionValidator implements Validatable {

  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    CustomDefinitionValidationUtil.onlyHasAllowedFields(fieldDefinition, TEXTBOX_ALLOWED_FIELDS);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return CustomField.Type.TEXTBOX_SHORT.equals(fieldDefinition.getType()) ||
      CustomField.Type.TEXTBOX_LONG.equals(fieldDefinition.getType());
  }
}
