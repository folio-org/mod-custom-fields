package org.folio.validate.definition;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;

import org.folio.rest.jaxrs.model.CustomField;


public class CustomDefinitionValidationUtil {

  /**
   * Throws exception if customField has non-null fields that are not in allowedFields collection
   * @param customField validated custom field definition
   * @param allowedFields field that can be not null
   * @throws IllegalArgumentException if falidation fails
   */
  public static void onlyHasAllowedFields(CustomField customField, Collection<String> allowedFields){
    List<Field> properties = FieldUtils.getFieldsListWithAnnotation(CustomField.class, JsonProperty.class);
    for (Field property : properties) {
      Validate.isTrue(isAllowedOrNull(property, customField, allowedFields),
        "Attribute %s is not allowed, following attributes are allowed for field of type %s : %s",
      property.getName(), customField.getType(), allowedFields);
    }
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
