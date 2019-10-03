package org.folio.validate.definition;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class NameValidator implements Validatable {

  @Value("${custom.fields.definition.name.length}")
  private int NAME_LENGTH_LIMIT;

  /**
   * Validates the length of the custom 'name' text attribute
   * @param fieldDefinition - the custom field definition
   * @throws IllegalArgumentException if validation fails
   */
  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    Validate.isTrue(fieldDefinition.getName().length() <= NAME_LENGTH_LIMIT,
      "The 'name' length cannot be more than %s", NAME_LENGTH_LIMIT);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return true;
  }
}
