package org.folio.rest.impl;

import org.apache.http.HttpStatus;
import org.folio.okapi.common.XOkapiHeaders;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.restassured.http.Header;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class CustomFieldsImplTest extends TestBase{

  private static final String FIELD_ID = "department";
  private static final Header USER = new Header(XOkapiHeaders.USER_ID, "88888888-8888-4888-8888-888888888888");

  private static final String CUSTOM_FIELDS_PATH = "custom-fields/";

  private static final String CUSTOM_FIELDS_ID_PATH = "custom-fields/" + FIELD_ID;

  @Test
  public void postCustomFields() {
    postWithStatus(CUSTOM_FIELDS_PATH, "{}", HttpStatus.SC_NOT_IMPLEMENTED, USER);
  }

  @Test
  public void getCustomFields() {
    getWithStatus(CUSTOM_FIELDS_PATH, HttpStatus.SC_NOT_IMPLEMENTED);
  }

  @Test
  public void getCustomFieldsById() {
    getWithStatus(CUSTOM_FIELDS_ID_PATH, HttpStatus.SC_NOT_IMPLEMENTED);
  }

  @Test
  public void deleteCustomFieldsById() {
    deleteWithStatus(CUSTOM_FIELDS_ID_PATH, HttpStatus.SC_NOT_IMPLEMENTED);
  }

  @Test
  public void putCustomFieldsById() {
    putWithStatus(CUSTOM_FIELDS_ID_PATH, "{}", HttpStatus.SC_NOT_IMPLEMENTED, USER);
  }
}
