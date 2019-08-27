package org.folio.repository;

public class CustomFieldsConstants {

  public static final String CUSTOM_FIELDS_TABLE = "custom_fields";
  public static final String JSONB_COLUMN = "jsonb";
  public static final String ID_COLUMN = "id";

  static final String COUNT_CUSTOM_FIELDS_BY_ID = "SELECT COUNT(*) as count from %s WHERE " + ID_COLUMN + " LIKE ?";
}
