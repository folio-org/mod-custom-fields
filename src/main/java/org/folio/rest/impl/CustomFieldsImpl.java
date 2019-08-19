package org.folio.rest.impl;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.resource.CustomFields;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class CustomFieldsImpl implements CustomFields {

  @Override
  public void postCustomFields(String lang, CustomField entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    asyncResultHandler.handle(Future.succeededFuture(GetCustomFieldsResponse.status(Response.Status.NOT_IMPLEMENTED).build()));
  }

  @Override
  public void getCustomFields(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    asyncResultHandler.handle(Future.succeededFuture(GetCustomFieldsResponse.status(Response.Status.NOT_IMPLEMENTED).build()));
  }

  @Override
  public void getCustomFieldsById(String id, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    asyncResultHandler.handle(Future.succeededFuture(GetCustomFieldsResponse.status(Response.Status.NOT_IMPLEMENTED).build()));
  }

  @Override
  public void deleteCustomFieldsById(String id, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    asyncResultHandler.handle(Future.succeededFuture(GetCustomFieldsResponse.status(Response.Status.NOT_IMPLEMENTED).build()));
  }

  @Override
  public void putCustomFieldsById(String id, String lang, CustomField entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    asyncResultHandler.handle(Future.succeededFuture(GetCustomFieldsResponse.status(Response.Status.NOT_IMPLEMENTED).build()));
  }
}
