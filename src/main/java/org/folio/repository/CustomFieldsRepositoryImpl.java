package org.folio.repository;

import static org.folio.repository.CustomFieldsConstants.COUNT_CUSTOM_FIELDS_BY_ID;
import static org.folio.repository.CustomFieldsConstants.CUSTOM_FIELDS_TABLE;

import java.util.Optional;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.persist.PostgresClient;

@Component
public class CustomFieldsRepositoryImpl implements CustomFieldsRepository {

  private final Logger logger = LoggerFactory.getLogger(CustomFieldsRepositoryImpl.class);

  @Autowired
  private Vertx vertx;

  /**
   * Saves a custom field to the database
   *
   * @param customField - current definition of the custom field {@link CustomField} object to save
   * @param tenantId - tenant id
   */
  @Override
  public Future<Optional<CustomField>> save(CustomField customField, String tenantId) {
    Future<String> future = Future.future();
    logger.debug("Saving a custom field with id: {}.", customField.getId());
    PostgresClient.getInstance(vertx, tenantId)
      .save(CUSTOM_FIELDS_TABLE,  customField.getId(), customField, future);
    return future.compose(customFieldId -> findById(customFieldId, tenantId));
  }

  /**
   * Fetches a custom field from the database
   *
   * @param id  - id of custom field to get
   * @param tenantId - tenant id
   */
  @Override
  public Future<Optional<CustomField>> findById(String id, String tenantId) {
    Future<CustomField> future = Future.future();
    logger.debug("Getting a custom field with id: {}.", id);
    PostgresClient.getInstance(vertx, tenantId)
      .getById(CUSTOM_FIELDS_TABLE, id, CustomField.class, future);

    return future.map(Optional::ofNullable);
  }

  /**
   * Fetches custom field ids from the database by given id using sql LIKE statement
   * @param customFieldId  - id of custom field
   * @param tenantId - tenant id
   */
  @Override
  public Future<Integer> countById(String customFieldId, String tenantId) {
    Future<ResultSet> future = Future.future();
    final String query = String.format(COUNT_CUSTOM_FIELDS_BY_ID, getCFTableName(tenantId));
    logger.debug("Getting custom field ids by given id: {}.", customFieldId);
    JsonArray parameters = new JsonArray().add(customFieldId + "_%");
    PostgresClient.getInstance(vertx, tenantId).select(query, parameters, future);
    return future.map(this::mapCount);
  }

  private Integer mapCount(ResultSet resultSet) {
    return resultSet.getRows().get(0).getInteger("count");
  }

  private String getCFTableName(String tenantId) {
    return PostgresClient.convertToPsqlStandard(tenantId) + "." + CUSTOM_FIELDS_TABLE;
  }

}
