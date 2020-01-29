package org.folio.validate.definition;

import static java.util.Objects.nonNull;

import static org.folio.validate.definition.AllowedFieldsConstants.TEXT_ALLOWED_FIELDS;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class TextFieldDefinitionValidator implements Validatable {

  public static final String TEXT_FIELD_DEFINING_MESSAGE = "The 'textField' property should be defined for '%s' custom field type.";
  public static final String MAX_SIZE_NON_NULL_MESSAGE = "The value for 'maxSize' should not be null.";
  public static final String MAX_SIZE_MUST_BE_POSITIVE_MESSAGE = "The value for 'maxSize' should be greater than 0.";

  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    CustomDefinitionValidationUtil.onlyHasAllowedFields(fieldDefinition, TEXT_ALLOWED_FIELDS);

    Validate.isTrue(nonNull(fieldDefinition.getTextField()), TEXT_FIELD_DEFINING_MESSAGE, fieldDefinition.getType());
    Validate.isTrue(nonNull(fieldDefinition.getTextField().getMaxSize()), MAX_SIZE_NON_NULL_MESSAGE);
    Validate.isTrue(fieldDefinition.getTextField().getMaxSize() > 0, MAX_SIZE_MUST_BE_POSITIVE_MESSAGE);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return CustomField.Type.TEXTBOX_SHORT.equals(fieldDefinition.getType()) ||
      CustomField.Type.TEXTBOX_LONG.equals(fieldDefinition.getType());
  }
}
