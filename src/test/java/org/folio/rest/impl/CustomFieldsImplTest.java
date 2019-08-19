package org.folio.rest.impl;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_IMPLEMENTED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.junit.Assert.assertEquals;

import static org.folio.test.util.TestUtil.readFile;

import java.io.IOException;
import java.net.URISyntaxException;

import io.restassured.http.Header;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.folio.okapi.common.XOkapiHeaders;
import org.folio.rest.jaxrs.model.CustomField;

@RunWith(VertxUnitRunner.class)
public class CustomFieldsImplTest extends TestBase {

  private static final String FIELD_ID = "department";
  private static final Header USER = new Header(XOkapiHeaders.USER_ID, "88888888-8888-4888-8888-888888888888");

  private static final String CUSTOM_FIELDS_PATH = "custom-fields";

  private static final String CUSTOM_FIELDS_ID_PATH = CUSTOM_FIELDS_PATH + "/" + FIELD_ID;


  @Before
  public void setUp() {
    DBTestUtil.deleteAllCustomFields(vertx);
  }

  @Test
  public void postCustomFields() throws IOException, URISyntaxException {
    final String cfWithAccentName = readFile("fields/post/postCustomFieldWithAccentName.json");
    final CustomField customField = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED, USER)
      .as(CustomField.class);

    assertEquals("this-is-a-tricky-string_1", customField.getId());
  }

  @Test
  public void shouldCreateTwoRefIdOnPostCustomFieldWithSameName() throws IOException, URISyntaxException {
    final String cfWithAccentName = readFile("fields/post/postCustomFieldWithAccentName.json");
    final CustomField customField_one = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED, USER)
      .as(CustomField.class);

    final CustomField customField_two = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED, USER)
      .as(CustomField.class);
    assertEquals("this-is-a-tricky-string_1", customField_one.getId());
    assertEquals("this-is-a-tricky-string_2", customField_two.getId());
  }

  @Test
  public void shouldCreateTwoRefIdOnPostCustomFieldWithSameName2() throws IOException, URISyntaxException {
    final String cfWithHalfName = readFile("fields/post/postCustomFieldHalfName.json");
    final String cfWithAccentName = readFile("fields/post/postCustomFieldWithAccentName.json");
    final CustomField customField_one = postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_CREATED, USER)
      .as(CustomField.class);

    final CustomField customField_two = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED, USER)
      .as(CustomField.class);
    assertEquals("this-is-a_1", customField_one.getId());
    assertEquals("this-is-a-tricky-string_1", customField_two.getId());
  }

  @Test
  public void shouldNotCreateCustomFieldWhenNameIsTooLong() throws IOException, URISyntaxException {
    final String cfWithHalfName = readFile("fields/post/postCustomNameWithTooLongName.json");
    postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_CREATED, USER);
  }

  @Test
  public void shouldReturn422WhenNameIsEmpty() throws IOException, URISyntaxException {
    final String cfWithHalfName = readFile("fields/post/postCustomFieldEmptyName.json");
    postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_UNPROCESSABLE_ENTITY, USER);
  }

  @Test
  public void shouldReturn422WhenTypeIsEmpty() throws IOException, URISyntaxException {
    final String cfWithHalfName = readFile("fields/post/postCustomFieldEmptyType.json");
    postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_UNPROCESSABLE_ENTITY, USER);
  }

  @Test
  public void getCustomFields() {
    getWithStatus(CUSTOM_FIELDS_PATH, SC_NOT_IMPLEMENTED);
  }

  @Test
  public void shouldReturnFieldOnValidId() throws IOException, URISyntaxException {
    final String postField = readFile("fields/post/postCustomField.json");

    DBTestUtil.insertCustomField(vertx, FIELD_ID, STUB_TENANT, postField);

    CustomField  field = getWithOk(CUSTOM_FIELDS_ID_PATH).as(CustomField.class);

    assertEquals("Department", field.getName());
    assertEquals("department", field.getId());
    assertEquals("Provide a department", field.getHelpText());
    assertEquals(true, field.getRequired());
    assertEquals(true, field.getVisible());
    assertEquals(CustomField.Type.SINGLE_CHECKBOX, field.getType());
  }

  @Test
  public void shouldReturn404OnMissingId() {
    getWithStatus(CUSTOM_FIELDS_ID_PATH, HttpStatus.SC_NOT_FOUND);
  }

  @Test
  public void deleteCustomFieldsById() {
    deleteWithStatus(CUSTOM_FIELDS_ID_PATH, SC_NOT_IMPLEMENTED);
  }

  @Test
  public void putCustomFieldsById() {
    putWithStatus(CUSTOM_FIELDS_ID_PATH, "{\"name\": \"test\", \"type\": \"SINGLE_CHECKBOX\"}", SC_NOT_IMPLEMENTED, USER);
  }
}
