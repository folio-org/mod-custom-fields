package org.folio.rest.impl;


import static org.folio.repository.CustomFieldsConstants.CUSTOM_FIELDS_TABLE;
import static org.folio.repository.CustomFieldsConstants.JSONB_COLUMN;
import static org.folio.rest.impl.TestBase.STUB_TENANT;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.persist.PostgresClient;

public class DBTestUtil {

  private DBTestUtil() {
  }

  public static void insertCustomField(Vertx vertx, String stubId, String tenantId, String json) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    PostgresClient.getInstance(vertx).execute(
      "INSERT INTO " + getCustomFieldsTableName(tenantId) + "(" + " id, " + JSONB_COLUMN + ") VALUES ('" + stubId + "' , '" + json + "');" ,
      event -> future.complete(null));
    future.join();
  }


  public static List<CustomField> getAllCustomFields(Vertx vertx) {
    ObjectMapper mapper = new ObjectMapper();
    CompletableFuture<List<CustomField>> future = new CompletableFuture<>();
    PostgresClient.getInstance(vertx).select(
      "SELECT * FROM " + getCustomFieldsTableName(STUB_TENANT),
      event -> future.complete(event.result().getRows().stream()
        .map(row -> row.getString(JSONB_COLUMN))
        .map(json -> parseCustomField(mapper, json))
        .collect(Collectors.toList())));
    return future.join();
  }

  public static void deleteAllCustomFields(Vertx vertx) {
    deleteFromTable(vertx, getCustomFieldsTableName(STUB_TENANT));
  }

  private static CustomField parseCustomField(ObjectMapper mapper, String json) {
    try {
      return mapper.readValue(json, CustomField.class);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Can't parse custom field", e);
    }
  }

  private static String getCustomFieldsTableName(String tenantId) {
    return PostgresClient.convertToPsqlStandard(tenantId) + "." + CUSTOM_FIELDS_TABLE;
  }

  private static void deleteFromTable(Vertx vertx, String tableName) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    PostgresClient.getInstance(vertx).execute(
      "DELETE FROM " + tableName,
      event -> future.complete(null));
    future.join();
  }
}
