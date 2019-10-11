package org.folio.validate.definition;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.SelectField;

public abstract class SelectableField {

  @Value("${custom.fields.option.name.length}")
  private int NAME_LENGTH_LIMIT;

  void validateOptions(CustomField fieldDefinition, int maxSize) {
    final SelectField selectField = fieldDefinition.getSelectField();

    final List<String> values = selectField.getOptions().getValues();

    if (!values.isEmpty()) {
      Validate.isTrue(values.size() <= maxSize,
        "The max option size for '" + fieldDefinition.getType() + "' custom field type is %s", maxSize);

      Validate.isTrue(values.stream().allMatch(value -> StringUtils.isNotBlank(value) && value.length() <= NAME_LENGTH_LIMIT),
        "The option name cannot be blank or have more than %s characters", NAME_LENGTH_LIMIT);
    }
  }

  void validateDefaults(CustomField fieldDefinition) {
    final SelectField selectField = fieldDefinition.getSelectField();

    if (Objects.nonNull(selectField.getDefaults())) {

      List<String> defaults = selectField.getDefaults();

      Validate.isTrue(!containsDuplicates(defaults), "The defaults list should not contain duplicate values");

      final List<String> values = fieldDefinition.getSelectField().getOptions().getValues();
      Validate.isTrue(values.containsAll(defaults), "The default value must be one of defined options: %s", values);
    }
  }

  void validateSingleDefaultSize(CustomField fieldDefinition) {

    final SelectField selectField = fieldDefinition.getSelectField();

    if (Objects.nonNull(selectField.getDefaults())) {

      List<String> defaults = selectField.getDefaults();
      Validate.isTrue(defaults.size() <= 1,
        "The max defaults size for '" + fieldDefinition.getType() + "' custom field type is %s", 1);
    }
  }

  void validateMultiSelectProperty(CustomField fieldDefinition, boolean expectedValue) {
    final SelectField selectField = fieldDefinition.getSelectField();

    final Boolean multiSelect = selectField.getMultiSelect();
    final CustomField.Type customFieldType = fieldDefinition.getType();

    Validate.isTrue(Objects.nonNull(multiSelect), "The value for 'multiSelect' should not be null.");
    Validate.isTrue(multiSelect == expectedValue,
      "The value for 'multiSelect' should be '"+ expectedValue + "' for '" + customFieldType + "' custom field type.");
  }

  private boolean containsDuplicates(List<String> defaults) {
      return new HashSet<>(defaults).size() != defaults.size();
  }
  void validateSelectFieldDefined(CustomField fieldDefinition) {
    Validate.isTrue(Objects.nonNull(fieldDefinition.getSelectField()),
      "The 'selectField' property should be defined for '" + fieldDefinition.getType() + "' custom field type.");
  }
}
