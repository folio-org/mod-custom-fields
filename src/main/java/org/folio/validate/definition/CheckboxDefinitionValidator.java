package org.folio.validate.definition;

import static java.util.Objects.nonNull;

import static org.folio.validate.definition.AllowedFieldsConstants.CHECKBOX_ALLOWED_FIELDS;
import static org.folio.validate.definition.CustomDefinitionValidationUtil.notSupportRepeatable;
import static org.folio.validate.definition.CustomDefinitionValidationUtil.onlyHasAllowedFields;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class CheckboxDefinitionValidator implements Validatable {

  public static final String CHECK_BOX_DEFINING_MESSAGE =
    "The 'checkBox' property should be defined for '%s' custom field type.";

  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    onlyHasAllowedFields(fieldDefinition, CHECKBOX_ALLOWED_FIELDS);
    notSupportRepeatable(fieldDefinition);
    Validate.isTrue(nonNull(fieldDefinition.getCheckboxField()), CHECK_BOX_DEFINING_MESSAGE, fieldDefinition.getType());
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return CustomField.Type.SINGLE_CHECKBOX.equals(fieldDefinition.getType());
  }
}
