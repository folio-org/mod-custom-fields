package org.folio.repository;

import javax.annotation.Nullable;

import static org.folio.repository.CustomFieldsConstants.CUSTOM_FIELDS_TABLE;
import static org.folio.repository.CustomFieldsConstants.REF_ID_REGEX;
import static org.folio.repository.CustomFieldsConstants.SELECT_MAX_ORDER;
import static org.folio.repository.CustomFieldsConstants.SELECT_REF_IDS;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

import org.apache.commons.lang.StringUtils;
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

  @Override
  public Future<CustomField> save(CustomField customField, String tenantId) {
    return save(customField, tenantId, null);
  }

  /**
   * Saves a custom field to the database
   *
   * @param customField - current definition of the custom field {@link CustomField} object to save
   * @param tenantId - tenant id
   */
  @Override
  public Future<CustomField> save(CustomField customField, String tenantId, @Nullable AsyncResult<SQLConnection> connection) {
    Promise<String> promise = Promise.promise();
    setIdIfMissing(customField);
    logger.debug("Saving a custom field with id: {}.", customField.getId());
    PostgresClient client = PostgresClient.getInstance(vertx, tenantId);
    if(connection != null){
      client.save(connection, CUSTOM_FIELDS_TABLE, customField.getId(), customField, promise);
    }
    else {
      client.save(CUSTOM_FIELDS_TABLE, customField.getId(), customField, promise);
    }

    return promise.future().map(id -> {
      customField.setId(id);
      return customField;
    }).recover(excTranslator.translateOrPassBy());
  }

  /**
   * Fetches a custom field from the database
   *
   * @param id  - id of custom field to get
   * @param tenantId - tenant id
   */
  @Override
  public Future<Optional<CustomField>> findById(String id, String tenantId) {
    Promise<CustomField> promise = Promise.promise();
    logger.debug("Getting a custom field with id: {}.", id);
    PostgresClient.getInstance(vertx, tenantId)
      .getById(CUSTOM_FIELDS_TABLE, id, CustomField.class, promise);

    return promise.future().map(Optional::ofNullable);
  }

  /**
   * Fetches the maximum custom field reference id from the database by given custom field name using sql regexp
   * @param customFieldName  - name of custom field
   * @param tenantId - tenant id
   */
  @Override
  public Future<Integer> maxRefId(String customFieldName, String tenantId,
                                  @Nullable AsyncResult<SQLConnection> connection) {
    Promise<ResultSet> promise = Promise.promise();
    final String query = String.format(SELECT_REF_IDS, getCFTableName(tenantId));
    String refIdRegex = String.format(REF_ID_REGEX, customFieldName);
    JsonArray parameters = new JsonArray().add(refIdRegex);
    logger.debug("Getting custom field ids by given name: {}.", customFieldName);
    PostgresClient client = PostgresClient.getInstance(vertx, tenantId);
    if (connection != null) {
      client.select(connection, query, parameters, promise);
    }
    else {
      client.select(query, parameters, promise);
    }
    return promise.future().map(this::mapMaxId);
  }

  @Override
  public Future<Integer> maxRefId(String customFieldName, String tenantId) {
    return maxRefId(customFieldName, tenantId, null);
  }

  @Override
  public Future<Integer> maxOrder(String tenantId) {
    Promise<ResultSet> promise = Promise.promise();
    final String query = String.format(SELECT_MAX_ORDER, getCFTableName(tenantId));
    logger.debug("Getting maximum order of custom custom fields.");
    PostgresClient.getInstance(vertx, tenantId).select(query, promise);
    return promise.future().map(this::mapMaxOrder);
  }

  @Override
  public Future<CustomFieldCollection> findByQuery(String query, int offset, int limit, String tenantId) {
    CqlQuery<CustomField> q = new CqlQuery<>(PostgresClient.getInstance(vertx, tenantId), CUSTOM_FIELDS_TABLE, CustomField.class);

    return q.get(query, offset, limit).map(this::toCustomFieldCollection);
  }

  @Override
  public Future<Boolean> update(CustomField entity, String tenantId) {
    return update(entity, tenantId, null);
  }

  @Override
  public Future<Boolean> update(CustomField entity, String tenantId, @Nullable AsyncResult<SQLConnection> connection) {
    Promise<UpdateResult> promise = Promise.promise();

    PostgresClient client = PostgresClient.getInstance(vertx, tenantId);
    if(connection != null){
      client.update(connection, CUSTOM_FIELDS_TABLE, entity,
        "jsonb", " WHERE id='" + entity.getId() + "'", false, promise);
    }
    else {
      client.update(CUSTOM_FIELDS_TABLE, entity, entity.getId(), promise);
    }
    return promise.future().map(updateResult -> updateResult.getUpdated() == 1)
      .recover(excTranslator.translateOrPassBy());
  }

  /**
   * Deletes custom field with given id from the database
   *
   * @param id - the id of the custom field
   * @param tenantId - tenant id
   */
  @Override
  public Future<Boolean> delete(String id, String tenantId, @Nullable AsyncResult<SQLConnection> connection) {
    Promise<UpdateResult> promise = Promise.promise();
    logger.debug("Deleting custom field by given id: {}.", id);
    if(connection != null){
      PostgresClient.getInstance(vertx, tenantId).delete(connection, CUSTOM_FIELDS_TABLE, id, promise);
    }else {
      PostgresClient.getInstance(vertx, tenantId).delete(CUSTOM_FIELDS_TABLE, id, promise);
    }
    return promise.future().map(updateResult -> updateResult.getUpdated() == 1)
      .recover(excTranslator.translateOrPassBy());
  }

  @Override
  public Future<Boolean> delete(String id, String tenantId) {
    return delete(id, tenantId, null);
  }

  private Integer mapMaxId(ResultSet resultSet) {
    return resultSet.getRows().stream()
      .map(row -> row.getString("values"))
      .mapToInt(value -> Integer.parseInt(value.substring(value.indexOf('_') + 1)))
      .max().orElse(0);
  }

  private Integer mapMaxOrder(ResultSet resultSet) {
    List<JsonObject> rows = resultSet.getRows();
    String maxOrder = rows.get(0).getString("max_order");
    return maxOrder != null ? Integer.parseInt(maxOrder) : 0;
  }

  private void setIdIfMissing(CustomField customField) {
    if (StringUtils.isBlank(customField.getId())) {
      customField.setId(UUID.randomUUID().toString());
    }
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
