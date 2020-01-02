package org.folio.repository;

import java.util.Optional;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.ext.sql.SQLConnection;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldCollection;

public interface CustomFieldsRepository {

  /**
   * Saves the definition of the custom field.
   * Returns newly created custom field.
   */
  Future<CustomField> save(CustomField customField, String tenantId);

  Future<CustomField> save(CustomField customField, String tenantId, AsyncResult<SQLConnection> connection);

  /**
   * Returns custom field with given id.
   * If custom field with given id doesn't exist then returns an empty Optional.
   */
  Future<Optional<CustomField>> findById(String id, String tenantId);

  /**
   * Returns a max count of custom fields by given customFieldName.
   */
  Future<Integer> maxRefId(String customFieldName, String tenantId);

  Future<Integer> maxRefId(String customFieldName, String tenantId,
                           AsyncResult<SQLConnection> connection);

  /**
   * Returns maximum value of "order" attribute in all custom fields,
   * or 0 if there are no fields with "order" attribute
   */
  Future<Integer> maxOrder(String tenantId);

  /**
   * Returns custom fields that match specified CQL query
   */
  Future<CustomFieldCollection> findByQuery(String query, int offset, int limit, String tenantId);

  Future<Boolean> update(CustomField entity, String tenantId);

  Future<Boolean> update(CustomField entity, String tenantId, AsyncResult<SQLConnection> connection);

  /**
   * Deletes custom field with given id.
   */
  Future<Boolean> delete(String id, String tenantId);
}
