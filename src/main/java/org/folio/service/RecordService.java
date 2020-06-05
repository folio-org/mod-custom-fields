package org.folio.service;

import io.vertx.core.Future;

import org.folio.model.RecordUpdate;
import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldStatistic;

public interface RecordService {

  Future<CustomFieldStatistic> retrieveStatistic(CustomField field, String tenantId);

  Future<Void> deleteAllValues(CustomField field, String tenantId);

  Future<Void> deleteMissedOptionValues(RecordUpdate recordUpdate, String tenantId);
}
