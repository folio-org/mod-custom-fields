package org.folio.rest.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NOT_IMPLEMENTED;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import static org.folio.test.util.TestUtil.STUB_TENANT;
import static org.folio.test.util.TestUtil.readFile;
import static org.folio.test.util.TestUtil.readJsonFile;

import java.io.IOException;
import java.net.URISyntaxException;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import io.restassured.http.Header;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.folio.okapi.common.XOkapiHeaders;
import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldCollection;
import org.folio.rest.jaxrs.model.Metadata;
import org.folio.test.util.TestBase;

@RunWith(VertxUnitRunner.class)
public class CustomFieldsImplTest extends TestBase {

  private static final String FIELD_ID = "department_1";
  private static final String FIELD_ID_2 = "expiration-date";

  private static final Header USER8 = new Header(XOkapiHeaders.USER_ID, "88888888-8888-4888-8888-888888888888");
  private static final Header USER9 = new Header(XOkapiHeaders.USER_ID, "99999999-9999-4999-9999-999999999999");

  private static final String CUSTOM_FIELDS_PATH = "custom-fields";
  private static final String CUSTOM_FIELDS_ID_PATH = CUSTOM_FIELDS_PATH + "/" + FIELD_ID;


  @Before
  public void setUp() throws IOException, URISyntaxException {
    CustomFieldsDBTestUtil.deleteAllCustomFields(vertx);


    stubFor(
      get(new UrlPathPattern(new EqualToPattern("/users/99999999-9999-4999-9999-999999999999"), false))
        .willReturn(new ResponseDefinitionBuilder()
          .withStatus(200)
          .withBody(readFile("users/mock_user.json"))
        ));

    stubFor(
      get(new UrlPathPattern(new EqualToPattern("/users/88888888-8888-4888-8888-888888888888"), false))
        .willReturn(new ResponseDefinitionBuilder()
          .withStatus(200)
          .withBody(readFile("users/mock_another_user.json"))
        ));

    stubFor(
      get(new UrlPathPattern(new EqualToPattern("/users/22999999-9999-4999-9999-999999999922"), false))
        .willReturn(new ResponseDefinitionBuilder()
          .withStatus(403))
    );

    stubFor(
      get(new UrlPathPattern(new EqualToPattern("/users/11999999-9999-4999-9999-999999999911"), false))
        .willReturn(new ResponseDefinitionBuilder()
          .withStatus(404))
    );
  }

  @Test
  public void postCustomFields() throws IOException, URISyntaxException {
    final String cfWithAccentName = readFile("fields/post/postCustomFieldWithAccentName.json");
    final CustomField customField = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED, USER8)
      .as(CustomField.class);

    assertEquals("this-is-a-tricky-string_1", customField.getId());

    final Metadata noteTypeMetadata = customField.getMetadata();

    assertEquals(USER8.getValue(), noteTypeMetadata.getCreatedByUserId());
    assertEquals("m8", noteTypeMetadata.getCreatedByUsername());
  }

  @Test
  public void shouldCreateTwoRefIdOnPostCustomFieldWithSameName() throws IOException, URISyntaxException {
    final String cfWithAccentName = readFile("fields/post/postCustomFieldWithAccentName.json");
    final CustomField customField_one = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED, USER8)
      .as(CustomField.class);

    final CustomField customField_two = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED, USER8)
      .as(CustomField.class);
    assertEquals("this-is-a-tricky-string_1", customField_one.getId());
    assertEquals("this-is-a-tricky-string_2", customField_two.getId());
  }

  @Test
  public void shouldCreateTwoRefIdOnPostCustomFieldWithSameName2() throws IOException, URISyntaxException {
    final String cfWithHalfName = readFile("fields/post/postCustomFieldHalfName.json");
    final String cfWithAccentName = readFile("fields/post/postCustomFieldWithAccentName.json");
    final CustomField customField_one = postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_CREATED, USER8)
      .as(CustomField.class);

    final CustomField customField_two = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED, USER8)
      .as(CustomField.class);
    assertEquals("this-is-a_1", customField_one.getId());
    assertEquals("this-is-a-tricky-string_1", customField_two.getId());
  }

  @Test
  public void shouldNotCreateCustomFieldWhenNameIsTooLong() throws IOException, URISyntaxException {
    final String cfWithHalfName = readFile("fields/post/postCustomNameWithTooLongName.json");
    postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_CREATED, USER8);
  }

  @Test
  public void shouldReturn422WhenNameIsEmpty() throws IOException, URISyntaxException {
    final String cfWithHalfName = readFile("fields/post/postCustomFieldEmptyName.json");
    postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_UNPROCESSABLE_ENTITY, USER8);
  }

  @Test
  public void shouldReturn422WhenTypeIsEmpty() throws IOException, URISyntaxException {
    final String cfWithHalfName = readFile("fields/post/postCustomFieldEmptyType.json");
    postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_UNPROCESSABLE_ENTITY);
  }

  @Test
  public void shouldReturn400WhenNoUserHeader() throws IOException, URISyntaxException {
    final String cfWithHalfName = readFile("fields/post/postCustomFieldHalfName.json");
    postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_BAD_REQUEST);
  }

  @Test
  public void shouldReturn401WhenUserUnautorized() throws IOException, URISyntaxException {
    final Header userWithoutPermission = new Header(XOkapiHeaders.USER_ID, "22999999-9999-4999-9999-999999999922");
    final String cfWithHalfName = readFile("fields/post/postCustomFieldHalfName.json");
    postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_UNAUTHORIZED, userWithoutPermission);
  }

  @Test
  public void shouldReturn404WhenUserNotFound() throws IOException, URISyntaxException {
    final Header userWithoutPermission = new Header(XOkapiHeaders.USER_ID, "11999999-9999-4999-9999-999999999911");
    final String cfWithHalfName = readFile("fields/post/postCustomFieldHalfName.json");
    postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_NOT_FOUND, userWithoutPermission);
  }

  @Test
  public void shouldReturnAllFieldsOnGet() throws IOException, URISyntaxException {
    createFields();
    CustomFieldCollection fields = getWithOk(CUSTOM_FIELDS_PATH).as(CustomFieldCollection.class);
    assertEquals(2, fields.getCustomFields().size());
    assertThat(fields.getCustomFields(), hasItem(allOf(
      hasProperty("name", is("Department")),
      hasProperty("helpText", is("Provide a department"))
    )));
    assertThat(fields.getCustomFields(), hasItem(allOf(
      hasProperty("name", is("Expiration Date")),
      hasProperty("helpText", is("Set expiration date"))
    )));
  }

  public void shouldReturnFieldsByName() throws IOException, URISyntaxException {
    createFields();
    CustomFieldCollection fields =
      getWithOk(CUSTOM_FIELDS_PATH + "?query=name==Department")
        .as(CustomFieldCollection.class);
    assertEquals(1, fields.getCustomFields().size());
    assertEquals(1, (int) fields.getTotalRecords());
    assertEquals("Department", fields.getCustomFields().get(0).getName());
    assertEquals("Provide a department", fields.getCustomFields().get(0).getHelpText());
  }

  @Test
  public void shouldReturnFieldsWithPagination() throws IOException, URISyntaxException {
    createFields();
    CustomFieldCollection fields =
      getWithOk(CUSTOM_FIELDS_PATH + "?offset=0&limit=1&query=cql.allRecords=1 sortby name")
        .as(CustomFieldCollection.class);
    assertEquals(1, fields.getCustomFields().size());
    assertEquals(2, (int) fields.getTotalRecords());
    assertEquals("Department", fields.getCustomFields().get(0).getName());
    assertEquals("Provide a department", fields.getCustomFields().get(0).getHelpText());
  }

  @Test
  public void shouldReturn400OnInvalidLimit() {
    getWithStatus(CUSTOM_FIELDS_PATH + "?limit=-1", SC_BAD_REQUEST);
  }

  @Test
  public void shouldReturn400OnInvalidQuery() {
    getWithStatus(CUSTOM_FIELDS_PATH + "?query=name~~abc", SC_BAD_REQUEST);
  }

  @Test
  public void shouldReturnFieldOnValidId() throws IOException, URISyntaxException {
    createFields();

    CustomField  field = getWithOk(CUSTOM_FIELDS_ID_PATH).as(CustomField.class);

    assertEquals("Department", field.getName());
    assertEquals(FIELD_ID, field.getId());
    assertEquals("Provide a department", field.getHelpText());
    assertEquals(true, field.getRequired());
    assertEquals(true, field.getVisible());
    assertEquals(CustomField.Type.SINGLE_CHECKBOX, field.getType());
  }

  @Test
  public void shouldReturn404OnMissingId() {
    getWithStatus(CUSTOM_FIELDS_ID_PATH, SC_NOT_FOUND);
  }

  public void getCustomFieldsById() throws IOException, URISyntaxException {
    final String postField = readFile("fields/post/postCustomField.json");

    DBTestUtil.insertCustomField(vertx, FIELD_ID, STUB_TENANT, postField);

    CustomField field = getWithOk(CUSTOM_FIELDS_ID_PATH).as(CustomField.class);

    assertEquals("Department", field.getName());
    assertEquals("department", field.getId());
    assertEquals("Provide a department", field.getHelpText());
    assertEquals(true, field.getRequired());
    assertEquals(true, field.getVisible());
    assertEquals(CustomField.Type.SINGLE_CHECKBOX, field.getType());
  }

  @Test
  public void shouldUpdateNoteNameTypeOnPut() throws IOException, URISyntaxException {
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER8);
    putWithNoContent(CUSTOM_FIELDS_ID_PATH, readFile("fields/post/putCustomField.json"), USER9);

    CustomField field = CustomFieldsDBTestUtil.getAllCustomFields(vertx).get(0);
    assertEquals("Department 2", field.getName());
    assertEquals(FIELD_ID, field.getId());
    assertEquals("Provide a second department", field.getHelpText());
    assertEquals(false, field.getRequired());
    assertEquals(false, field.getVisible());
    assertEquals(CustomField.Type.RADIO_BUTTON, field.getType());

    final Metadata noteTypeMetadata = field.getMetadata();

    assertEquals(USER8.getValue(), noteTypeMetadata.getCreatedByUserId());
    assertEquals("m8", noteTypeMetadata.getCreatedByUsername());

    assertEquals(USER9.getValue(), noteTypeMetadata.getUpdatedByUserId());
    assertEquals("mockuser9", noteTypeMetadata.getUpdatedByUsername());
  }

  @Test
  public void deleteCustomFieldsById() {
    deleteWithStatus(CUSTOM_FIELDS_ID_PATH, SC_NOT_IMPLEMENTED);
  }

  private void createFields() throws IOException, URISyntaxException {
    CustomField postField2 = readJsonFile("fields/post/postCustomField2.json", CustomField.class);
    CustomField postField = readJsonFile("fields/post/postCustomField.json", CustomField.class);
    CustomFieldsDBTestUtil.saveCustomField(FIELD_ID, postField, vertx);
    CustomFieldsDBTestUtil.saveCustomField(FIELD_ID_2, postField2, vertx);
  }
}
