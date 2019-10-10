package org.folio.validate.definition;

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
public abstract class SelectableField {

  @Value("${custom.fields.definition.name.length}")
  private int NAME_LENGTH_LIMIT;

  @Value("${custom.fields.definition.single.default.size}")
  private int SINGLE_DEFAULT_SIZE;

  @Value("#{${custom.fields.definition.option.size}}")
  private Map<String, Integer> optionsSizeMap;

  void validateOptions(CustomField fieldDefinition) {
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

  void validateDefaults(CustomField fieldDefinition) {
    final SelectField selectField = fieldDefinition.getSelectField();

    if (Objects.nonNull(selectField) && Objects.nonNull(selectField.getDefaults())) {

      List<String> defaults = selectField.getDefaults();

      if (!defaults.isEmpty()) {
        Validate.isTrue(Objects.nonNull(fieldDefinition.getSelectField().getOptions()), "Options can not be null if 'defaults' defined.");

        final List<String> values = fieldDefinition.getSelectField().getOptions().getValues();
        Validate.isTrue(values.size() >= defaults.size(), "The defaults size can not be more than options number");
        Validate.isTrue(values.containsAll(defaults), "The default value must be one of defined options: %s", values);
      }
    }
  }

  void validateSingleDefaultSize(CustomField fieldDefinition) {

    final SelectField selectField = fieldDefinition.getSelectField();

    if (Objects.nonNull(selectField) && Objects.nonNull(selectField.getDefaults())) {

      List<String> defaults = selectField.getDefaults();
      Validate.isTrue(defaults.size() <= 1,
        "The max defaults size for '" + fieldDefinition.getType() + "' custom field type is %s", 1);
    }
  }

  void validateMultiSelectProperty(CustomField fieldDefinition) {
    final SelectField selectField = fieldDefinition.getSelectField();

    if (Objects.nonNull(selectField)) {

      final Boolean multiSelect = selectField.getMultiSelect();
      final CustomField.Type customFieldType = fieldDefinition.getType();

      Validate.isTrue(Objects.nonNull(multiSelect), "The value for 'multiSelect' should not be null.");
      if (CustomField.Type.MULTI_SELECT_DROPDOWN.equals(customFieldType)) {
        Validate.isTrue(multiSelect,
          "The value for 'multiSelect' should be 'true' for '" + customFieldType + "' custom field type.");
      } else {
        Validate.isTrue(!multiSelect,
          "The value for 'multiSelect' should be 'false' for '" + customFieldType + "' custom field type.");
      }
    }
  }
}
