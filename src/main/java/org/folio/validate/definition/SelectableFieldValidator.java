package org.folio.validate.definition;

import static org.apache.commons.collections4.IterableUtils.countMatches;
import static org.apache.commons.collections4.IterableUtils.matchesAll;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.Validate.isTrue;

import static org.folio.service.CustomFieldUtils.extractOptionIds;
import static org.folio.service.CustomFieldUtils.extractOptionValues;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.SelectFieldOption;

public abstract class SelectableFieldValidator {

  private static final String BLANK_OPTION_MESSAGE = "The option value cannot be blank or have more than %s length";
  private static final String BLANK_SELECT_FIELD_PROP_MESSAGE = "The 'selectField' property should be defined";
  private static final String ILLEGAL_MULTI_SELECT_PROP_MESSAGE = "The 'multiSelect' property should be '%s'";
  private static final String MAX_DEFAULTS_AMOUNT_MESSAGE = "The max defaults amount is %d";
  private static final String MAX_OPTION_SIZE_MESSAGE = "The options amount should be in range %d - %d";
  private static final String NOT_UNIQUE_OPTIONS_IDS_MESSAGE = "Option IDs should be unique";
  private static final String NOT_UNIQUE_OPTIONS_VALUES_MESSAGE = "Option values should be unique";
  private static final String NULL_OPTION_MESSAGE = "Option should not be null";
  private static final String NULL_VALUES_PROP_MESSAGE = "The 'values' property should not be null";

  @Value("${custom.fields.option.name.length}")
  private int nameLengthLimit;

  protected void validateSelectFieldDefined(CustomField fieldDefinition) {
    isTrue(Objects.nonNull(fieldDefinition.getSelectField()), BLANK_SELECT_FIELD_PROP_MESSAGE);
  }

  protected void validateOptions(CustomField fieldDefinition, int maxOptionSize) {
    final List<SelectFieldOption> options = fieldDefinition.getSelectField().getOptions().getValues();

    validateOptionsAmount(options, maxOptionSize);
    validateNullOptions(options);
    validateOptionValues(fieldDefinition);
    validateOptionIds(fieldDefinition);
  }

  protected void validateSingleSelectDefaultsAmount(CustomField fieldDefinition) {
    final List<SelectFieldOption> options = fieldDefinition.getSelectField().getOptions().getValues();
    long defaultsCount = countMatches(options, SelectFieldOption::getDefault);
    isTrue(defaultsCount <= 1, MAX_DEFAULTS_AMOUNT_MESSAGE, 1);
  }

  protected void validateMultiSelectProperty(CustomField fieldDefinition, boolean expectedValue) {
    final Boolean multiSelect = fieldDefinition.getSelectField().getMultiSelect();
    boolean validMultiSelect = Boolean.valueOf(expectedValue).equals(multiSelect);
    isTrue(validMultiSelect, ILLEGAL_MULTI_SELECT_PROP_MESSAGE, expectedValue);
  }

  private void validateOptionsAmount(List<SelectFieldOption> options, int maxOptionSize) {
    isTrue(options != null, NULL_VALUES_PROP_MESSAGE);
    boolean isValidOptionsAmount = !options.isEmpty() && options.size() <= maxOptionSize;
    isTrue(isValidOptionsAmount, MAX_OPTION_SIZE_MESSAGE, 1, maxOptionSize);
  }

  private void validateNullOptions(List<SelectFieldOption> options) {
    boolean hasNullOptions = options.stream().allMatch(Objects::nonNull);
    isTrue(hasNullOptions, NULL_OPTION_MESSAGE);
  }

  private void validateOptionValues(CustomField cf) {
    List<String> optionValues = extractOptionValues(cf);
    boolean isValidOptionValuesLength = matchesAll(optionValues, this::checkValueLength);
    isTrue(isValidOptionValuesLength, BLANK_OPTION_MESSAGE, nameLengthLimit);

    boolean areUniqueOptionValues = allOptionValuesAreUnique(optionValues);
    isTrue(areUniqueOptionValues, NOT_UNIQUE_OPTIONS_VALUES_MESSAGE, nameLengthLimit);
  }

  private void validateOptionIds(CustomField cf) {
    final List<String> optionIds = extractOptionIds(cf);
    optionIds.removeIf(Objects::isNull);
    int uniqueIdCount = new HashSet<>(optionIds).size();
    isTrue(uniqueIdCount == optionIds.size(), NOT_UNIQUE_OPTIONS_IDS_MESSAGE);
  }

  private boolean checkValueLength(String value) {
    return isNotBlank(value) && value.length() <= nameLengthLimit;
  }

  private boolean allOptionValuesAreUnique(List<String> optionValues) {
    return new HashSet<>(optionValues).size() == optionValues.size();
  }
}
