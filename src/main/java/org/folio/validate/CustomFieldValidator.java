package org.folio.validate;

import org.folio.rest.jaxrs.model.CustomField;

public interface CustomFieldValidator {

  /**
   * Validates custom field
   * @param fieldValue object that was parsed from json, type of object is String or Map<String, Object>
   * @param fieldDefinition field definition that will be used to validate value
   * @throws IllegalArgumentException if validation fails
   */
  void validate(Object fieldValue, CustomField fieldDefinition);

  /**
   * @return Type of custom field that can be processed by this validator
   */
  CustomField.Type supportedType();
}
