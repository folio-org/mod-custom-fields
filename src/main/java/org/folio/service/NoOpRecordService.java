package org.folio.service;

import static io.vertx.core.Future.succeededFuture;

import io.vertx.core.Future;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldStatistic;

public final class NoOpRecordService implements RecordService {

  @Override
  public Future<CustomFieldStatistic> retrieveStatistic(CustomField field, String tenantId) {
    return succeededFuture(
      new CustomFieldStatistic()
        .withFieldId(field.getId())
        .withEntityType(field.getEntityType())
        .withCount(0)
    );
  }

  @Override
  public Future<Void> deleteAllValues(CustomField field, String tenantId) {
    return succeededFuture();
  }

}
