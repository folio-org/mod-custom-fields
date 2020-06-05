package org.folio.validate.value;

import static org.apache.commons.lang3.Validate.isInstanceOf;
import static org.apache.commons.lang3.Validate.isTrue;

import static org.folio.validate.value.CustomFieldValueValidatorConstants.EXPECT_ARRAY_MESSAGE;
import static org.folio.validate.value.CustomFieldValueValidatorConstants.EXPECT_STRING_MESSAGE;
import static org.folio.validate.value.CustomFieldValueValidatorConstants.NOT_ALLOWED_VALUE_MESSAGE;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.SelectFieldOption;

@Component
public class SelectFieldValueValidator implements CustomFieldValueValidator {

  @Override
  public void validate(Object fieldValue, CustomField fieldDefinition) {
    boolean isRepeatable = fieldDefinition.getIsRepeatable();
    CustomField.Type type = fieldDefinition.getType();

    if (isRepeatable) {
      validateFieldList(fieldValue, fieldDefinition);
    } else {
      if (CustomField.Type.MULTI_SELECT_DROPDOWN == type) {
        if (fieldValue instanceof List) {
          validateFieldList(fieldValue, fieldDefinition);
        } else {
          validateField(fieldValue, fieldDefinition);
        }
      } else {
        validateField(fieldValue, fieldDefinition);
      }
    }
  }

  private void validateFieldList(Object fieldValue, CustomField fieldDefinition) {
    isInstanceOf(List.class, fieldValue, EXPECT_ARRAY_MESSAGE, fieldDefinition.getType());
    ((List<?>) fieldValue).forEach(val -> validateField(val, fieldDefinition));
  }

  private void validateField(Object fieldValue, CustomField fieldDefinition) {
    isInstanceOf(String.class, fieldValue, EXPECT_STRING_MESSAGE, fieldDefinition.getType());
    String optionId = (String) fieldValue;
    List<SelectFieldOption> possibleValues = fieldDefinition.getSelectField().getOptions().getValues();
    boolean isAllowedValue = possibleValues
      .stream().anyMatch(selectFieldOption -> selectFieldOption.getId().equals(optionId));
    isTrue(isAllowedValue, NOT_ALLOWED_VALUE_MESSAGE, fieldDefinition.getRefId(), possibleValues);
  }

  @Override
  public List<CustomField.Type> supportedTypes() {
    return Arrays.asList(
      CustomField.Type.SINGLE_SELECT_DROPDOWN,
      CustomField.Type.MULTI_SELECT_DROPDOWN,
      CustomField.Type.RADIO_BUTTON
    );
  }
}
