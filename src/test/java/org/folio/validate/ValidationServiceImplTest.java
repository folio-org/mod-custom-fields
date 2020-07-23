package org.folio.validate;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.folio.CustomFieldsTestUtil.CUSTOM_FIELDS_PATH;
import static org.folio.CustomFieldsTestUtil.USER1_HEADER;
import static org.folio.CustomFieldsTestUtil.mockUserRequests;
import static org.folio.test.util.TestUtil.STUB_TENANT;
import static org.folio.test.util.TestUtil.readFile;

import java.io.IOException;
import java.net.URISyntaxException;

import org.folio.CustomFieldsTestUtil;
import org.folio.rest.jaxrs.model.Errors;
import org.folio.rest.jaxrs.model.Parameter;
import org.folio.spring.SpringContextUtil;
import org.folio.test.util.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import io.vertx.core.Context;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ValidationServiceImplTest extends TestBase {

  private ValidationService validationService;

  @Autowired
  private Context vertxContext;

  @Before
  public void setUp() throws IOException, URISyntaxException {
    SpringContextUtil.autowireDependenciesFromFirstContext(this, vertx);
    validationService = new ValidationServiceImpl(vertxContext);
    mockUserRequests();
  }

  @After
  public void tearDown() {
    CustomFieldsTestUtil.deleteAllCustomFields(vertx);
  }

  @Test
  public void shouldReturnSuccessfulFutureIfValidationSucceeds(TestContext context) throws IOException, URISyntaxException {
    createRadioButtonField();
    Async async = context.async();
    CustomFieldValue customFieldValue = Json.decodeValue("{\"favoritefood\":\"opt_1\"}", CustomFieldValue.class);
    validationService
      .validateCustomFields(customFieldValue.getAdditionalProperties(), STUB_TENANT)
      .map(o -> {
        async.complete();
        return null;
      })
      .otherwise(e -> {
        context.fail(e);
        return null;
      });
  }

  @Test
  public void shouldReturnAnErrorIfValidationFails(TestContext context) throws IOException, URISyntaxException {
    createRadioButtonField();
    Async async = context.async();
    CustomFieldValue customFieldValue = Json.decodeValue("{\"favoritefood\":\"opt_5\"}", CustomFieldValue.class);
    validationService.validateCustomFields(customFieldValue.getAdditionalProperties(), STUB_TENANT)
      .onComplete(validation -> {
        if (validation.failed()) {
          Throwable cause = validation.cause();
          if (!(cause instanceof CustomFieldValidationException)) {
            context.fail(cause);
          } else {
            Errors errors = ((CustomFieldValidationException) cause).getErrors();
            Parameter errorParam = errors.getErrors().get(0).getParameters().get(0);
            context.assertEquals("favoritefood", errorParam.getKey());
            context.assertEquals("\"opt_5\"", errorParam.getValue());
          }
        } else {
          context.fail("Validation didn't fail.");
        }
        async.complete();
      });
  }

  @Test
  public void shouldReturnAnErrorIfFieldIsNotFound(TestContext context) throws IOException, URISyntaxException {
    createRadioButtonField();
    Async async = context.async();
    CustomFieldValue customFieldValue = Json.decodeValue("{\"notexistingfield\":\"value\"}", CustomFieldValue.class);
    validationService
      .validateCustomFields(customFieldValue.getAdditionalProperties(), STUB_TENANT)
      .otherwise(e -> {
        if (!(e instanceof CustomFieldValidationException)) {
          context.fail(e);
          return null;
        }
        Errors errors = ((CustomFieldValidationException) e).getErrors();
        Parameter errorParam = errors.getErrors().get(0).getParameters().get(0);
        context.assertEquals("customFields", errorParam.getKey());
        context.assertEquals("notexistingfield", errorParam.getValue());
        async.complete();
        return null;
      });
  }

  private void createRadioButtonField() throws IOException, URISyntaxException {
    String radioButton = readFile("fields/post/radioButton/postValidRadioButton.json");
    postWithStatus(CUSTOM_FIELDS_PATH, radioButton, SC_CREATED, USER1_HEADER);
  }
}
