package org.folio.repository;

import java.util.Optional;

import io.vertx.core.Future;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldCollection;

public interface CustomFieldsRepository {

  /**
   * Saves the definition of the custom field.
   * Returns newly created custom field.
   */
  Future<Optional<CustomField>> save(CustomField customField, String tenantId);

  /**
   * Returns custom field with given id.
   * If custom field with given id doesn't exist then returns an empty Optional.
   */
  Future<Optional<CustomField>> findById(String id, String tenantId);

  /**
   * Returns count of custom fields with given customFieldId.
   */
  Future<Integer> countById(String customFieldId, String tenantId);
  /**
   * Returns custom fields that match specified CQL query
   */
  Future<CustomFieldCollection> findByQuery(String query, int offset, int limit, String tenantId);
}
