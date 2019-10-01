package org.folio.validate.definition;

import org.folio.rest.jaxrs.model.CustomField;

public interface DefinitionValidationService {

  /**
   * Validates custom field definition
   * @param fieldDefinition - the custom field definition
   * @throws IllegalArgumentException if validation fails
   */
  void validateDefinition(CustomField fieldDefinition);

  /**
   * Defines if the validation can be applied to the field
   * @param fieldDefinition - the custom field definition
   */
  boolean isApplicable(CustomField fieldDefinition);
}
