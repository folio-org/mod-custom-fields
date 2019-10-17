package org.folio.rest.impl;

import static io.vertx.core.Future.failedFuture;
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

import org.folio.common.OkapiParams;
import org.folio.common.pf.PartialFunction;
import org.folio.rest.annotations.Validate;
import org.folio.rest.aspect.HandleValidationErrors;
import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldCollection;
import org.folio.rest.jaxrs.model.CustomFieldStatisticCollection;
import org.folio.rest.jaxrs.resource.CustomFields;
import org.folio.service.CustomFieldsService;
import org.folio.spring.SpringContextUtil;
import org.folio.validate.definition.DefinitionValidator;

public class CustomFieldsImpl implements CustomFields {

  @Autowired
  private CustomFieldsService customFieldsService;

  @Autowired @Qualifier("customFieldsExcHandler")
  private PartialFunction<Throwable, Response> excHandler;

  @Autowired
  private DefinitionValidator definitionValidator;

  public CustomFieldsImpl() {
    SpringContextUtil.autowireDependencies(this, Vertx.currentContext());
  }

  @Override
  @HandleValidationErrors
  public void postCustomFields(String lang, CustomField entity, Map<String, String> okapiHeaders,
                               Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    definitionValidator.validate(entity);
    final Future<CustomField> saved = customFieldsService.save(entity, new OkapiParams(okapiHeaders));
    respond(saved,
      customField -> PostCustomFieldsResponse.respond201WithApplicationJson(customField, headersFor201()),
      asyncResultHandler, excHandler);
  }

  @Override
  @Validate
  public void getCustomFields(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders,
                              Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

    Future<CustomFieldCollection> found = customFieldsService.findByQuery(query, offset, limit, lang, tenantId(okapiHeaders));
    respond(found, GetCustomFieldsResponse::respond200WithApplicationJson, asyncResultHandler, excHandler);
  }

  @Override
  @Validate
  public void getCustomFieldsById(String id, String lang, Map<String, String> okapiHeaders,
                                  Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    Future<CustomField> field = customFieldsService.findById(id, tenantId(okapiHeaders));
    respond(field, GetCustomFieldsByIdResponse::respond200WithApplicationJson, asyncResultHandler, excHandler);
  }

  @Override
  @Validate
  public void deleteCustomFieldsById(String id, String lang, Map<String, String> okapiHeaders,
                                     Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

    Future<Void> deleted = customFieldsService.delete(id, tenantId(okapiHeaders));
    respond(deleted, v -> DeleteCustomFieldsByIdResponse.respond204(), asyncResultHandler, excHandler);
  }

  @Override
  @HandleValidationErrors
  public void putCustomFieldsById(String id, String lang, CustomField entity, Map<String, String> okapiHeaders,
                                  Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    definitionValidator.validate(entity);

    Future<Void> updated = customFieldsService.findById(id, tenantId(okapiHeaders))
      .compose(customField -> {
        if (!customField.getType().equals(entity.getType())) {
          return failedFuture(new IllegalArgumentException("The type of the custom field can not be changed."));
        }
        return succeededFuture();
      })
      .compose(o -> customFieldsService.update(id, entity, new OkapiParams(okapiHeaders)));
    respond(updated, v -> PutCustomFieldsByIdResponse.respond204(), asyncResultHandler, excHandler);
  }

  @Override
  @Validate
  public void getCustomFieldsStatsById(String id, String lang, Map<String, String> okapiHeaders,
                                       Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    Future<CustomFieldStatisticCollection> stats = customFieldsService.retrieveStatistic(id, tenantId(okapiHeaders));

    respond(stats, GetCustomFieldsStatsByIdResponse::respond200WithApplicationJson, asyncResultHandler, excHandler);
  }
}
