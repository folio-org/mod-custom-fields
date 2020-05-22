package org.folio.repository;

public final class CustomFieldsConstants {

  public static final String CUSTOM_FIELDS_TABLE = "custom_fields";

  public static final String MAX_ORDER_COLUMN = "max_order";
  public static final String VALUES_COLUMN = "values";
  public static final String JSONB_COLUMN = "jsonb";
  public static final String ID_COLUMN = "id";

  public static final String REF_ID_REGEX = "%s_[1-9]+";
  public static final String SELECT_REF_IDS = "SELECT unnest(regexp_matches(" + JSONB_COLUMN + " ->> 'refId', $1)) as "
    + VALUES_COLUMN + " FROM %s";
  public static final String SELECT_MAX_ORDER = "SELECT MAX((jsonb ->> 'order')::int) as " + MAX_ORDER_COLUMN + " FROM %s";
  public static final String WHERE_ID_EQUALS_CLAUSE = "WHERE " + ID_COLUMN + "='%s'";

  private CustomFieldsConstants() {
  }
}
