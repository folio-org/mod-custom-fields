package org.folio.rest.aspect;

import org.folio.rest.annotations.Validate;
import org.folio.validate.ValidationUtil;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Future;
import java.lang.IllegalArgumentException;

import org.aspectj.lang.annotation.SuppressAjWarnings;

import javax.ws.rs.core.Response;

public aspect ValidationErrorHandlerAspect {
  pointcut validatedMethodCall(Handler<AsyncResult<Response>> asyncResultHandler) : execution(@HandleValidationErrors * *(.., Handler, *)) && args(.., asyncResultHandler, *);

  @SuppressAjWarnings({"adviceDidNotMatch"})
  void around(Handler asyncResultHandler) : validatedMethodCall(asyncResultHandler) {
    try{
      proceed(asyncResultHandler);
    }
    catch (IllegalArgumentException e){
            asyncResultHandler.handle(Future.succeededFuture(Response.status(422)
            .header("Content-Type", "application/json")
            .entity(ValidationUtil.createError(null, null, e.getMessage()))
            .build()));
            return;
    }
  }
}
