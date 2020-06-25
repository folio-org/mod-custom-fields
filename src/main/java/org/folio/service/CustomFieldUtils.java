package org.folio.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.SelectFieldOption;

public final class CustomFieldUtils {

  private CustomFieldUtils() {}

  public static List<String> extractOptionIds(CustomField customField) {
    return extractOptionParameter(customField, SelectFieldOption::getId);
  }

  public static List<String> extractDefaultOptionIds(CustomField customField) {
    List<String> defaultOptionIds = extractOptionParameter(customField,
      selectFieldOption -> Boolean.TRUE.equals(selectFieldOption.getDefault()) ? selectFieldOption.getId() : null);
    defaultOptionIds.removeIf(Objects::isNull);
    return defaultOptionIds;
  }

  public static List<String> extractOptionValues(CustomField customField) {
    return extractOptionParameter(customField, SelectFieldOption::getValue);
  }

  public static boolean isSelectableCustomFieldType(CustomField customField) {
    CustomField.Type type = customField.getType();
    return CustomField.Type.SINGLE_SELECT_DROPDOWN.equals(type)
      || CustomField.Type.MULTI_SELECT_DROPDOWN.equals(type)
      || CustomField.Type.RADIO_BUTTON.equals(type);
  }

  public static boolean isTextBoxCustomFieldType(CustomField customField) {
    CustomField.Type type = customField.getType();
    return CustomField.Type.TEXTBOX_LONG.equals(type) || CustomField.Type.TEXTBOX_SHORT.equals(type);
  }

  private static <T> List<T> extractOptionParameter(CustomField cf, Function<SelectFieldOption, T> extractFunction) {
    if (isSelectableCustomFieldType(cf)) {
      return cf.getSelectField().getOptions().getValues().stream()
        .filter(Objects::nonNull)
        .map(extractFunction)
        .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
}
