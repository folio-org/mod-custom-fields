package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.folio.rest.jaxrs.resource.CustomFields;

public class CustomFieldsImpl implements CustomFields {
  @Override
  public void getCustomFields(String query, int offset, int limit, Map<String, String> okapiHeaders,
                              Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {

    asyncResultHandler.handle(Future.succeededFuture(GetCustomFieldsResponse.status(Response.Status.NOT_IMPLEMENTED).build()));

  }
}
