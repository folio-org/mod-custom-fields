package org.folio.validate.definition;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class CommonDefinitionValidator {

  private static final int NAME_LENGTH_LIMIT = 65;
  private static final int HELP_TEXT_LENGTH_LIMIT = 100;
  @Autowired
  private List<DefinitionValidationService> validators;

  public void validate(CustomField fieldDefinition) {
    validateName(fieldDefinition);
    validateHelpText(fieldDefinition);
    validateByType(fieldDefinition);
  }

  /**
   * Validates the length of the custom field 'helpText' attribute
   * @param fieldDefinition - the custom field definition
   * @throws IllegalArgumentException if validation fails
   */
  private void validateHelpText(CustomField fieldDefinition) {
    Validate.isTrue(fieldDefinition.getHelpText().length() <= HELP_TEXT_LENGTH_LIMIT,
      "The 'helpText' length cannot be more than %s",  HELP_TEXT_LENGTH_LIMIT);
  }

  /**
   * Validates the length of the custom 'name' text attribute
   * @param fieldDefinition - the custom field definition
   * @throws IllegalArgumentException if validation fails
   */
  private void validateName(CustomField fieldDefinition) {
    Validate.isTrue(fieldDefinition.getName().length() <= NAME_LENGTH_LIMIT,
      "The 'name' length cannot be more than %s", NAME_LENGTH_LIMIT);
  }

  /**
   * Find appropriate custom field validator by type and performs validation
   * @param fieldDefinition - the custom field definition
   */
  private void validateByType(CustomField fieldDefinition) {
      validators.stream()
      .filter(validator -> validator.isApplicable(fieldDefinition))
      .findFirst()
      .ifPresent(validator -> validator.validateDefinition(fieldDefinition));
  }
}
