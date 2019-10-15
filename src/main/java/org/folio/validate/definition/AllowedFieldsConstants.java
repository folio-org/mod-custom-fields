package org.folio.validate.definition;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class AllowedFieldsConstants {
  public static final Set<String> COMMON_ALLOWED_FIELDS = ImmutableSet.of(
    "id",
    "name",
    "refId",
    "type",
    "entityType",
    "visible",
    "required",
    "helpText",
    "metadata"
  );

  public static final Set<String> CHECKBOX_ALLOWED_FIELDS = new ImmutableSet.Builder<String>()
    .addAll(COMMON_ALLOWED_FIELDS).add("checkboxField")
    .build();
  public static final Set<String> TEXT_ALLOWED_FIELDS = new ImmutableSet.Builder<String>()
    .addAll(COMMON_ALLOWED_FIELDS).add("textField")
    .build();
  public static final Set<String> SELECT_ALLOWED_FIELDS = new ImmutableSet.Builder<String>()
    .addAll(COMMON_ALLOWED_FIELDS).add("selectField")
    .build();

}

