package org.folio.validate.definition;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;

@Component
public class HelpTextValidator  implements Validatable {

  @Value("${custom.fields.definition.helpText.length}")
  private int HELP_TEXT_LENGTH_LIMIT;

  /**
   * Validates the length of the custom field 'helpText' attribute
   * @param fieldDefinition - the custom field definition
   * @throws IllegalArgumentException if validation fails
   */
  @Override
  public void validateDefinition(CustomField fieldDefinition) {
    Validate.isTrue(fieldDefinition.getHelpText().length() <= HELP_TEXT_LENGTH_LIMIT,
      "The 'helpText' length cannot be more than %s",  HELP_TEXT_LENGTH_LIMIT);
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return true;
  }
}
