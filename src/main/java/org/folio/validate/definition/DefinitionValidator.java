package org.folio.validate.definition;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class DefinitionValidator {

  @Autowired
  private List<Validatable> validators;

  /**
   * Finds applicable custom field validators by type and performs validation
   * @param fieldDefinition - the custom field definition
   */
  public void validate(CustomField fieldDefinition) {
    validators.stream()
      .filter(validator -> validator.isApplicable(fieldDefinition))
      .forEach(validator -> validator.validateDefinition(fieldDefinition));
  }
}
