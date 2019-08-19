package org.folio.service;

import io.vertx.core.Future;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldCollection;

public interface CustomFieldsService {

  /**
   * Saves the definition of the custom field.
   * Returns newly created custom field.
   */
  Future<CustomField> save(CustomField customField, String tenantId);

  /**
   * Returns custom field with given id.
   * If custom field with given id doesn't exist then returns failed Future with NotFoundException as a cause.
   */
  Future<CustomField> findById(String id, String tenantId);

  Future<CustomFieldCollection> findByQuery(String query, int offset, int limit, String lang, String tenantId);
}
