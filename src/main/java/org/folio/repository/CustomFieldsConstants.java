package org.folio.repository;

public class CustomFieldsConstants {

  public static final String CUSTOM_FIELDS_TABLE = "custom_fields";
  public static final String JSONB_COLUMN = "jsonb";
  public static final String ID_COLUMN = "id";

  public static final String REF_ID_REGEX = "(%s_[1-9]{1,})";
  public static final String SELECT_REF_IDS = "SELECT unnest(regexp_matches(" + JSONB_COLUMN + " ->> 'refId', ?)) as values FROM %s";
  public static final String SELECT_MAX_ORDER = "SELECT MAX(jsonb->>'order') as max_order FROM %s";

  public static final String FIND_CF_BY_ORDER_QUERY = "query=order==%d";
}
