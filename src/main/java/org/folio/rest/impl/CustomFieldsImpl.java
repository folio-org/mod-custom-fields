package org.folio.rest.impl;

import static io.vertx.core.Future.succeededFuture;

import static org.folio.rest.ResponseHelper.respond;
import static org.folio.rest.jaxrs.resource.CustomFields.PostCustomFieldsResponse.headersFor201;
import static org.folio.rest.tools.utils.TenantTool.tenantId;

import java.util.Map;

import javax.ws.rs.core.Response;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.folio.common.pf.PartialFunction;
import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.resource.CustomFields;
import org.folio.service.CustomFieldsService;
import org.folio.spring.SpringContextUtil;

public class CustomFieldsImpl implements CustomFields {

  @Autowired
  private CustomFieldsService customFieldsService;

  @Autowired @Qualifier("customFieldsExcHandler")
  private PartialFunction<Throwable, Response> excHandler;

  public CustomFieldsImpl() {
    SpringContextUtil.autowireDependencies(this, Vertx.currentContext());
  }

  @Override
  public void postCustomFields(String lang, CustomField entity, Map<String, String> okapiHeaders,
                               Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    final Future<CustomField> saved = customFieldsService.save(entity, tenantId(okapiHeaders));
    respond(saved,
      customField -> PostCustomFieldsResponse.respond201WithApplicationJson(customField, headersFor201()),
      asyncResultHandler, excHandler);
  }

  @Override
  public void getCustomFields(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders,
                              Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    asyncResultHandler.handle(succeededFuture(GetCustomFieldsResponse.status(Response.Status.NOT_IMPLEMENTED)
        .build()));
  }

  @Override
  public void getCustomFieldsById(String id, String lang, Map<String, String> okapiHeaders,
                                  Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    Future<CustomField> field = customFieldsService.findById(id, tenantId(okapiHeaders));
    respond(field, GetCustomFieldsByIdResponse::respond200WithApplicationJson, asyncResultHandler, excHandler);
  }

  @Override
  public void deleteCustomFieldsById(String id, String lang, Map<String, String> okapiHeaders,
                                     Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    asyncResultHandler.handle(succeededFuture(GetCustomFieldsResponse.status(Response.Status.NOT_IMPLEMENTED)
        .build()));
  }

  @Override
  public void putCustomFieldsById(String id, String lang, CustomField entity, Map<String, String> okapiHeaders,
                                  Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    asyncResultHandler.handle(succeededFuture(GetCustomFieldsResponse.status(Response.Status.NOT_IMPLEMENTED)
        .build()));
  }
}
