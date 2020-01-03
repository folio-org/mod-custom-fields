package org.folio.service;

import io.vertx.core.Future;

import org.folio.common.OkapiParams;
import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldCollection;
import org.folio.rest.jaxrs.model.CustomFieldStatistic;

import java.util.List;

public interface CustomFieldsService {

  /**
   * Saves the definition of the custom field.
   * Returns newly created custom field.
   */
  Future<CustomField> save(CustomField customField, OkapiParams params);

  /**
   * Updates the definition of the custom field with specified id
   * @param id id of field to update
   * @param customField new value
   * @param params OkapiParams with tenantId
   */
  Future<Void> update(String id, CustomField customField, OkapiParams params);

  /**
   * Returns custom field with given id.
   * If custom field with given id doesn't exist then returns failed Future with NotFoundException as a cause.
   */
  Future<CustomField> findById(String id, String tenantId);

  Future<CustomFieldCollection> findByQuery(String query, int offset, int limit, String lang, String tenantId);

  /**
   * Deletes custom field with given id.
   */
  Future<Void> delete(String id, String tenantId);

  /**
   * Replaces all existing custom fields with new collection of custom fields.
   * If new collection has fields with ids that already exist then those fields will be updated,
   * fields with ids that don't exist will be added,
   * existing fields that are not present in new collection will be deleted
   * @param newFields collection of new custom fields
   * @param params OkapiParams
   * @return updated collection of custom fields
   */
  Future<List<CustomField>> replaceAll(List<CustomField> newFields, OkapiParams params);

  Future<CustomFieldStatistic> retrieveStatistic(String id, String tenantId);
}
