package org.folio.service;

import io.vertx.core.Future;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldStatisticCollection;

public interface RecordService {

  Future<CustomFieldStatisticCollection> retrieveStatistic(CustomField field, String tenantId);

  Future<Void> deleteAllValues(CustomField field, String tenantId);
}
