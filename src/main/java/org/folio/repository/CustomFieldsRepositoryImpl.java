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
import io.vertx.ext.sql.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.folio.db.CqlQuery;
import org.folio.db.exc.translation.DBExceptionTranslator;
import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldCollection;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.interfaces.Results;

@Component
public class CustomFieldsRepositoryImpl implements CustomFieldsRepository {

  private final Logger logger = LoggerFactory.getLogger(CustomFieldsRepositoryImpl.class);

  @Autowired
  private Vertx vertx;
  @Autowired
  private DBExceptionTranslator excTranslator;

  /**
   * Saves a custom field to the database
   *
   * @param customField - current definition of the custom field {@link CustomField} object to save
   * @param tenantId - tenant id
   */
  @Override
  public Future<CustomField> save(CustomField customField, String tenantId) {
    Future<String> future = Future.future();
    logger.debug("Saving a custom field with id: {}.", customField.getId());
    PostgresClient.getInstance(vertx, tenantId)
      .save(CUSTOM_FIELDS_TABLE,  customField.getId(), customField, future);

    return future.map(customField).recover(excTranslator.translateOrPassBy());
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

  @Override
  public Future<CustomFieldCollection> findByQuery(String query, int offset, int limit, String tenantId) {
    CqlQuery<CustomField> q = new CqlQuery<>(PostgresClient.getInstance(vertx, tenantId), CUSTOM_FIELDS_TABLE, CustomField.class);

    return q.get(query, offset, limit).map(this::toCustomFieldCollection);
  }

  @Override
  public Future<Boolean> update(CustomField entity, String tenantId) {
    Future<UpdateResult> future = Future.future();

    PostgresClient.getInstance(vertx, tenantId).update(CUSTOM_FIELDS_TABLE, entity, entity.getId(), future);

    return future.map(updateResult -> updateResult.getUpdated() == 1)
      .recover(excTranslator.translateOrPassBy());
  }

  private Integer mapCount(ResultSet resultSet) {
    return resultSet.getRows().get(0).getInteger("count");
  }

  private String getCFTableName(String tenantId) {
    return PostgresClient.convertToPsqlStandard(tenantId) + "." + CUSTOM_FIELDS_TABLE;
  }

  private CustomFieldCollection toCustomFieldCollection(Results<CustomField> results) {
    return new CustomFieldCollection()
      .withCustomFields(results.getResults())
      .withTotalRecords(results.getResultInfo().getTotalRecords());
  }
}
