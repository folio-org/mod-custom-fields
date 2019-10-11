package org.folio.rest.exceptions;

import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;

import static org.folio.common.pf.PartialFunctions.pf;
import static org.folio.rest.exc.ExceptionPredicates.instanceOf;

import javax.ws.rs.core.Response;

import org.folio.common.pf.PartialFunction;
import org.folio.rest.ResponseHelper;
import org.folio.rest.jaxrs.model.Error;
import org.folio.validate.ValidationUtil;

public class CustomFieldTypeExceptionHandler {

  private CustomFieldTypeExceptionHandler() {}

  public static PartialFunction<Throwable, Response> customFieldTypeValidationHandler() {
    return pf(instanceOf(IllegalArgumentException.class)
      .and(throwable -> {
      IllegalArgumentException exc = (IllegalArgumentException) throwable;
       return "The type of the custom field can not be changed.".equals(exc.getMessage());
    })
    , CustomFieldTypeExceptionHandler::toUnprocessableEntity);
  }

  private static Response toUnprocessableEntity(Throwable t) {
    IllegalArgumentException exc = (IllegalArgumentException) t;
    final Error errorMessage = ValidationUtil.createError(
      null, null, exc.getMessage());
    return ResponseHelper.statusWithJson(SC_UNPROCESSABLE_ENTITY, errorMessage);
  }
}
