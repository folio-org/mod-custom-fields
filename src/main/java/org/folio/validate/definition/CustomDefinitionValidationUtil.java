package org.folio.validate.definition;

import static org.apache.commons.lang3.Validate.isTrue;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.reflect.FieldUtils;

import org.folio.rest.jaxrs.model.CustomField;


class CustomDefinitionValidationUtil {

  private CustomDefinitionValidationUtil() {
  }

  /**
   * Throws exception if customField has non-null fields that are not in allowedFields collection
   *
   * @param customField   validated custom field definition
   * @param allowedFields field that can be not null
   * @throws IllegalArgumentException if falidation fails
   */
  static void onlyHasAllowedFields(CustomField customField, Collection<String> allowedFields) {
    List<Field> properties = FieldUtils.getFieldsListWithAnnotation(CustomField.class, JsonProperty.class);
    for (Field property : properties) {
      isTrue(isAllowedOrNull(property, customField, allowedFields),
        "Attribute %s is not allowed, following attributes are allowed for field of type %s : %s",
        property.getName(), customField.getType(), allowedFields);
    }
  }

  /**
   * Throws exception if customField has isRepeatable=true.
   * Needed to use when validate customField that not supports repeatable feature.
   *
   * @param customField validated custom field definition
   */
  static void notSupportRepeatable(CustomField customField) {
    boolean isValid = customField.getIsRepeatable() != null && !customField.getIsRepeatable();
    isTrue(isValid, "Repeatable is not supported by custom field of type %s", customField.getType());
  }

  private static boolean isAllowedOrNull(Field property, CustomField customField, Collection<String> allowedFields) {
    return allowedFields.contains(property.getName()) || Objects.isNull(getFieldValue(customField, property));
  }

  private static Object getFieldValue(CustomField customField, Field field) {
    try {
      return FieldUtils.readField(field, customField, true);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }
}
