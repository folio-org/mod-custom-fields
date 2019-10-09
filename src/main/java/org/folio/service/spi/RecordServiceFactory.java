package org.folio.service.spi;

import io.vertx.core.Vertx;

import org.folio.service.RecordService;

public interface RecordServiceFactory {

  RecordService create(Vertx vertx);

}
