package org.folio.validate;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.apache.http.HttpStatus.SC_CREATED;

import static org.folio.test.util.TestUtil.STUB_TENANT;
import static org.folio.test.util.TestUtil.readFile;

import java.io.IOException;
import java.net.URISyntaxException;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import io.restassured.http.Header;
import io.vertx.core.Context;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.folio.okapi.common.XOkapiHeaders;
import org.folio.rest.impl.DBTestUtil;
import org.folio.rest.jaxrs.model.Errors;
import org.folio.rest.jaxrs.model.Parameter;
import org.folio.spring.SpringContextUtil;
import org.folio.test.util.TestBase;

@RunWith(VertxUnitRunner.class)
public class ValidationServiceImplTest extends TestBase {

  private static final String CUSTOM_FIELDS_PATH = "custom-fields";
  private static final Header USER8 = new Header(XOkapiHeaders.USER_ID, "99999999-9999-4999-9999-999999999999");

  private ValidationService validationService;

  @Autowired
  private Context vertxContext;

  @Before
  public void setUp() throws IOException, URISyntaxException {
    SpringContextUtil.autowireDependenciesFromFirstContext(this, vertx);
    stubFor(
      get(new UrlPathPattern(new EqualToPattern("/users/99999999-9999-4999-9999-999999999999"), false))
        .willReturn(new ResponseDefinitionBuilder()
          .withStatus(200)
          .withBody(readFile("users/mock_user.json"))
        ));

    validationService = new ValidationServiceImpl(vertxContext);
  }

  @After
  public void tearDown() {
    DBTestUtil.deleteAllCustomFields(vertx);
  }

  @Test
  public void shouldReturnSuccessfulFutureIfValidationSucceeds(TestContext context) throws IOException, URISyntaxException {
    createRadioButtonField();
    Async async = context.async();
    CustomFieldValue customFieldValue = Json.decodeValue("{\"favoritefood_1\":\"pizza\"}", CustomFieldValue.class);
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
    CustomFieldValue customFieldValue = Json.decodeValue("{\"favoritefood_1\":\"table\"}", CustomFieldValue.class);
    validationService
      .validateCustomFields( customFieldValue.getAdditionalProperties(), STUB_TENANT)
      .otherwise(e -> {
        if(!(e instanceof CustomFieldValidationException)){
          context.fail(e);
          return null;
        }
        Errors errors = ((CustomFieldValidationException) e).getErrors();
        Parameter errorParam = errors.getErrors().get(0).getParameters().get(0);
        context.assertEquals("favoritefood_1", errorParam.getKey());
        context.assertEquals("\"table\"", errorParam.getValue());
        async.complete();
        return null;
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
        if(!(e instanceof CustomFieldValidationException)){
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
    String radioButton = readFile("fields/post/radioButton/postCustomFieldRadioButton.json");
    postWithStatus(CUSTOM_FIELDS_PATH, radioButton, SC_CREATED, USER8);
  }
}
