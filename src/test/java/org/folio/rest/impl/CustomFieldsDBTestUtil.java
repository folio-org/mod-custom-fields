package org.folio.rest.impl;

import static org.folio.repository.CustomFieldsConstants.CUSTOM_FIELDS_TABLE;
import static org.folio.test.util.DBTestUtil.deleteFromTable;
import static org.folio.test.util.DBTestUtil.getAll;
import static org.folio.test.util.DBTestUtil.save;

import java.util.List;

import io.vertx.core.Vertx;

import org.folio.rest.jaxrs.model.CustomField;

public class CustomFieldsDBTestUtil {

  private CustomFieldsDBTestUtil() {
  }

  public static void deleteAllCustomFields(Vertx vertx) {
    deleteFromTable(vertx, CUSTOM_FIELDS_TABLE);
  }

  public static List<CustomField> getAllCustomFields(Vertx vertx) {
    return getAll(CustomField.class, vertx, CUSTOM_FIELDS_TABLE);
  }

  public static void saveCustomField(String id, CustomField customField, Vertx vertx) {
    save(id, customField, vertx, CUSTOM_FIELDS_TABLE);
  }
}
