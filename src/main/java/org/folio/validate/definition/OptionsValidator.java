package org.folio.validate.definition;

import static org.folio.rest.jaxrs.model.CustomField.Type.RADIO_BUTTON;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.SelectField;

@Component
public class OptionsValidator implements Validatable {

  @Value("${custom.fields.definition.name.length}")
  private int NAME_LENGTH_LIMIT;

  @Value("#{${custom.fields.definition.option.size}}")
  private Map<String, Integer> optionsSizeMap;

  @Override
  public void validateDefinition(CustomField fieldDefinition) {

    final SelectField selectField = fieldDefinition.getSelectField();

    if (Objects.nonNull(selectField) && Objects.nonNull(selectField.getOptions())) {

      final List<String> values = selectField.getOptions().getValues();
      final Integer maxOptionSize = optionsSizeMap.get(fieldDefinition.getType().value());

      if(!values.isEmpty()) {
        Validate.isTrue(values.size() <= maxOptionSize,
          "The max option size for '" + fieldDefinition.getType() + "' custom field type is %s", maxOptionSize);

        Validate.isTrue(values.stream().allMatch(value -> StringUtils.isNotBlank(value) && value.length() <= NAME_LENGTH_LIMIT),
          "The option name cannot be blank or have more than %s characters", NAME_LENGTH_LIMIT);
      }
    }
  }

  @Override
  public boolean isApplicable(CustomField fieldDefinition) {
    return RADIO_BUTTON.equals(fieldDefinition.getType());
  }
}
