package org.folio.rest.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.folio.rest.impl.CustomFieldsDBTestUtil.saveCustomField;
import static org.folio.test.util.TestUtil.STUB_TENANT;
import static org.folio.test.util.TestUtil.readFile;
import static org.folio.test.util.TestUtil.readJsonFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

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
import org.folio.rest.jaxrs.model.CustomFieldStatisticCollection;
import org.folio.rest.jaxrs.model.Error;
import org.folio.rest.jaxrs.model.Metadata;
import org.folio.test.util.TestBase;

@RunWith(VertxUnitRunner.class)
public class CustomFieldsImplTest extends TestBase {

  private static final String STUB_FIELD_ID = "11111111-1111-1111-a111-111111111111";
  private static final String STUB_FIELD_ID_2 = "22222222-2222-2222-a222-222222222222";

  private static final Header USER8 = new Header(XOkapiHeaders.USER_ID, "88888888-8888-4888-8888-888888888888");
  private static final Header USER9 = new Header(XOkapiHeaders.USER_ID, "99999999-9999-4999-9999-999999999999");

  private static final String CUSTOM_FIELDS_PATH = "custom-fields";
  private static final String CUSTOM_FIELDS_ID_PATH = CUSTOM_FIELDS_PATH + "/" + STUB_FIELD_ID;


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

    assertEquals("this-is-a-tricky-string_1", customField.getRefId());

    final Metadata noteTypeMetadata = customField.getMetadata();

    assertEquals(USER8.getValue(), noteTypeMetadata.getCreatedByUserId());
    assertEquals("m8", noteTypeMetadata.getCreatedByUsername());
  }

  @Test
  public void shouldCreateTwoRefIdOnPostCustomFieldWithSameName() throws IOException, URISyntaxException {
    final String cfWithAccentName = readFile("fields/post/postCustomFieldWithAccentName.json");
    final CustomField customField_one = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED, USER8)
      .as(CustomField.class);
    final String cfWithAccentName2 = readFile("fields/post/postCustomFieldWithAccentNameSecond.json");
    final CustomField customField_two = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName2, SC_CREATED, USER8)
      .as(CustomField.class);
    assertEquals("this-is-a-tricky-string_1", customField_one.getRefId());
    assertEquals("this-is-a-tricky-string_2", customField_two.getRefId());
  }

  @Test
  public void shouldCreateTwoRefIdOnPostCustomFieldWithSameName2() throws IOException, URISyntaxException {
    final String cfWithAccentName = readFile("fields/post/postCustomFieldWithAccentName.json");
    final String cfWithHalfName = readFile("fields/post/postCustomFieldHalfName2.json");
    final CustomField customField_one = postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_CREATED, USER8)
      .as(CustomField.class);

    final CustomField customField_two = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED, USER8)
      .as(CustomField.class);
    assertEquals("this-is-a_1", customField_one.getRefId());
    assertEquals("this-is-a-tricky-string_1", customField_two.getRefId());
  }

  @Test
  public void shouldNotCreateCustomFieldWhenNameIsTooLongOnPost() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postCustomNameWithTooLongName.json");
    postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY, USER8);
  }

  @Test
  public void shouldReturn422WhenNameIsEmptyOnPost() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postCustomFieldEmptyName.json");
    postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY, USER8);
  }

  @Test
  public void shouldReturn422WhenTypeIsEmptyOnPost() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postCustomFieldEmptyType.json");
    postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY);
  }

  @Test
  public void shouldReturn422WhenEntityTypeIsEmptyOnPost() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postCustomFieldEmptyEntityType.json");
    postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY);
  }

  @Test
  public void shouldReturnErrorIfHelpTextTooLong() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postCustomFieldHelpTextInvalid.json");
    postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY);
  }

  @Test
  public void shouldReturnErrorIfInvalidCustomFieldType() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postInvalidCustomField.json");
    postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_BAD_REQUEST);
  }

  @Test
  public void shouldReturn400WhenNoUserHeader() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postCustomFieldHalfName.json");
    postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_BAD_REQUEST);
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
      hasProperty("helpText", is("Provide a department")),
      hasProperty("entityType", is("user"))
    )));
    assertThat(fields.getCustomFields(), hasItem(allOf(
      hasProperty("name", is("Expiration Date")),
      hasProperty("helpText", is("Set expiration date")),
      hasProperty("entityType", is("package"))
    )));
  }

  @Test
  public void shouldReturnFieldsByName() throws IOException, URISyntaxException {
    createFields();
    CustomFieldCollection fields =
      getWithOk(CUSTOM_FIELDS_PATH + "?query=name==Department")
        .as(CustomFieldCollection.class);
    assertEquals(1, fields.getCustomFields().size());
    assertEquals(1, (int) fields.getTotalRecords());
    assertEquals("Department", fields.getCustomFields().get(0).getName());
    assertEquals("Provide a department", fields.getCustomFields().get(0).getHelpText());
    assertEquals("user", fields.getCustomFields().get(0).getEntityType());
  }

  @Test
  public void shouldReturnFieldsByEntityType() throws IOException, URISyntaxException {
    createFields();
    CustomFieldCollection fields =
      getWithOk(CUSTOM_FIELDS_PATH + "?query=entityType==package")
        .as(CustomFieldCollection.class);
    assertEquals(1, fields.getCustomFields().size());
    assertEquals(1, (int) fields.getTotalRecords());
    assertEquals("Expiration Date", fields.getCustomFields().get(0).getName());
    assertEquals("Set expiration date", fields.getCustomFields().get(0).getHelpText());
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
    final CustomField customField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER8)
        .as(CustomField.class);

    CustomField  field = getWithOk(CUSTOM_FIELDS_PATH + "/" + customField.getId()).as(CustomField.class);

    assertEquals("Department", field.getName());
    assertEquals("department_1", field.getRefId());
    assertEquals("Provide a department", field.getHelpText());
    assertEquals(true, field.getRequired());
    assertEquals(true, field.getVisible());
    assertEquals(CustomField.Type.SINGLE_CHECKBOX, field.getType());
  }

  @Test
  public void shouldReturn422ErrorWhenOrderIsInvalid() throws IOException, URISyntaxException {
    String customFieldFile = readFile("fields/post/postCustomField.json");

    postWithStatus(CUSTOM_FIELDS_PATH, customFieldFile, SC_CREATED, USER8);
    final Error error = postWithStatus(CUSTOM_FIELDS_PATH, customFieldFile, SC_UNPROCESSABLE_ENTITY, USER8).as(Error.class);

    assertEquals("Order number should be unique.", error.getMessage());
  }

  @Test
  public void shouldReturn404OnMissingId() {
    getWithStatus(CUSTOM_FIELDS_ID_PATH, SC_NOT_FOUND);
  }

  @Test
  public void getCustomFieldsById() throws IOException, URISyntaxException {
    final String postField = readFile("fields/post/postCustomField.json");

    DBTestUtil.insertCustomField(vertx, STUB_FIELD_ID, STUB_TENANT, postField);

    CustomField field = getWithOk(CUSTOM_FIELDS_ID_PATH).as(CustomField.class);

    assertEquals("Department", field.getName());
    assertNotNull(field.getRefId());
    assertEquals("Provide a department", field.getHelpText());
    assertEquals(true, field.getRequired());
    assertEquals(true, field.getVisible());
    assertEquals(CustomField.Type.SINGLE_CHECKBOX, field.getType());
  }

  @Test
  public void shouldUpdateNoteNameTypeOnPut() throws IOException, URISyntaxException {

    final CustomField customField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER8)
        .as(CustomField.class);
    putWithNoContent(CUSTOM_FIELDS_PATH + "/" + customField.getId(), readFile("fields/put/putCustomField.json"), USER9);

    CustomField field = CustomFieldsDBTestUtil.getAllCustomFields(vertx).get(0);
    assertEquals("Department 2", field.getName());
    assertEquals("department-_1", field.getRefId());
    assertEquals("Provide a second department", field.getHelpText());
    assertEquals(false, field.getRequired());
    assertEquals(false, field.getVisible());
    assertEquals(CustomField.Type.SINGLE_CHECKBOX, field.getType());

    final Metadata noteTypeMetadata = field.getMetadata();

    assertEquals(USER8.getValue(), noteTypeMetadata.getCreatedByUserId());
    assertEquals("m8", noteTypeMetadata.getCreatedByUsername());

    assertEquals(USER9.getValue(), noteTypeMetadata.getUpdatedByUserId());
    assertEquals("mockuser9", noteTypeMetadata.getUpdatedByUsername());
  }

  @Test
  public void shouldNotChangeRefIdWhenNameIsSameOnPut() throws IOException, URISyntaxException {
    final CustomField customField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField2.json"), SC_CREATED, USER8)
        .as(CustomField.class);
    putWithNoContent(CUSTOM_FIELDS_PATH + "/" + customField.getId(), readFile("fields/put/putCustomField2.json"), USER9);

    CustomField field = CustomFieldsDBTestUtil.getAllCustomFields(vertx).get(0);
    assertEquals("Expiration Date", field.getName());
    assertEquals("expiration-date_1", field.getRefId());
    assertEquals("Set expiration date", field.getHelpText());
    assertEquals(true, field.getRequired());
    assertEquals(true, field.getVisible());
    assertEquals(CustomField.Type.TEXTBOX_SHORT, field.getType());

    final Metadata noteTypeMetadata = field.getMetadata();

    assertEquals(USER8.getValue(), noteTypeMetadata.getCreatedByUserId());
    assertEquals("m8", noteTypeMetadata.getCreatedByUsername());

    assertEquals(USER9.getValue(), noteTypeMetadata.getUpdatedByUserId());
    assertEquals("mockuser9", noteTypeMetadata.getUpdatedByUsername());
  }

  @Test
  public void shouldReturn422WhenCustomFieldTypeChangedOnPut() throws IOException, URISyntaxException {
    final CustomField customField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER8)
        .as(CustomField.class);
    putWithStatus(CUSTOM_FIELDS_PATH + "/" + customField.getId(),
      readFile("fields/put/putCustomField2.json"), SC_UNPROCESSABLE_ENTITY, USER9);
  }

  @Test
  public void shouldReturn422WhenNameIsEmptyOnPut() throws IOException, URISyntaxException {
    final CustomField customField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER8)
        .as(CustomField.class);

    final String cfWithHalfName = readFile("fields/put/putCustomFieldEmptyName.json");
    putWithStatus(CUSTOM_FIELDS_PATH + "/" + customField.getId(), cfWithHalfName, SC_UNPROCESSABLE_ENTITY, USER8);
  }

  @Test
  public void shouldReturn422WhenTypeIsEmptyOnPut() throws IOException, URISyntaxException {
    final CustomField customField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER8)
        .as(CustomField.class);

    final String cfWithHalfName = readFile("fields/put/putCustomFieldEmptyType.json");
    putWithStatus(CUSTOM_FIELDS_PATH + "/" + customField.getId(), cfWithHalfName, SC_UNPROCESSABLE_ENTITY);
  }

  @Test
  public void shouldReturn422WhenEntityTypeIsEmptyOnPut() throws IOException, URISyntaxException {
    final CustomField customField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER8)
        .as(CustomField.class);

    final String cfWithHalfName = readFile("fields/put/putCustomFieldEmptyEntityType.json");
    putWithStatus(CUSTOM_FIELDS_PATH + "/" + customField.getId(), cfWithHalfName, SC_UNPROCESSABLE_ENTITY);
  }

  @Test
  public void deleteCustomFieldsById() throws IOException, URISyntaxException {
    final CustomField customFieldOne = postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER8)
      .as(CustomField.class);

    deleteWithNoContent(CUSTOM_FIELDS_PATH + "/" + customFieldOne.getId());
    deleteWithStatus(CUSTOM_FIELDS_ID_PATH, SC_NOT_FOUND);
  }

  @Test
  public void deleteCustomFieldAndReorderLast() throws IOException, URISyntaxException {
    final CustomField customFieldOne = postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER8)
      .as(CustomField.class);
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomFieldOrder2.json"), SC_CREATED, USER8);
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomFieldOrder3.json"), SC_CREATED, USER8);

    deleteWithNoContent(CUSTOM_FIELDS_PATH + "/" + customFieldOne.getId());

    CustomFieldCollection fields = getWithOk(CUSTOM_FIELDS_PATH).as(CustomFieldCollection.class);
    assertEquals(2, fields.getCustomFields().size());
    assertEquals(1, (int) fields.getCustomFields().get(0).getOrder());
    assertEquals(2, (int) fields.getCustomFields().get(1).getOrder());
  }

  @Test
  public void deleteCustomFieldAndReorderLast1212() throws IOException, URISyntaxException {
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomFieldOrder3.json"), SC_CREATED, USER8);
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER8);
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomFieldOrder2.json"), SC_CREATED, USER8);

    deleteWithStatus(CUSTOM_FIELDS_PATH + "/11111111-2222-3333-a444-555555555555" , SC_NOT_FOUND);

    CustomFieldCollection fields = getWithOk(CUSTOM_FIELDS_PATH + "?query=cql.allRecords=1 sortby order").as(CustomFieldCollection.class);
    assertEquals(3, fields.getCustomFields().size());
    assertEquals(1, (int) fields.getCustomFields().get(0).getOrder());
    assertEquals(2, (int) fields.getCustomFields().get(1).getOrder());
    assertEquals(3, (int) fields.getCustomFields().get(2).getOrder());
  }

  @Test
  public void shouldReturn404WhenDeleteRequestHasNotExistingUUID() {
    deleteWithStatus(CUSTOM_FIELDS_PATH + "/11111111-2222-3333-a444-555555555555", SC_NOT_FOUND);
  }

  @Test
  public void shouldReturn400WhenDeleteRequestHasInvalidUUID() {
    deleteWithStatus(CUSTOM_FIELDS_PATH + "/11111111-3-1111-333-111111111111", SC_BAD_REQUEST);
  }

  @Test
  public void shouldDeleteAndGenerateNewRefId() throws IOException, URISyntaxException {
    final CustomField customFieldOne = postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER8)
      .as(CustomField.class);

    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomFieldOrder2.json"), SC_CREATED, USER8);

    deleteWithNoContent(CUSTOM_FIELDS_PATH + "/" + customFieldOne.getId());

    final CustomField customFieldThree =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomFieldOrder3.json"), SC_CREATED, USER8)
        .as(CustomField.class);

    assertTrue(customFieldThree.getRefId().endsWith("_3"));

  }

  @Test
  public void shouldReturnEmptyStatsForExistingField() throws IOException, URISyntaxException {
    final CustomField field = postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER8)
      .as(CustomField.class);

    CustomFieldStatisticCollection stats = getWithOk(
      CUSTOM_FIELDS_PATH + "/" + field.getId() + "/stats").as(CustomFieldStatisticCollection.class);

    assertEquals(stats, new CustomFieldStatisticCollection()
      .withStats(Collections.emptyList())
      .withTotalRecords(0));
  }

  @Test
  public void shouldFailWith404WhenStatsRequestedForNonExistingField() throws IOException, URISyntaxException {
    getWithStatus(CUSTOM_FIELDS_PATH + "/" + STUB_FIELD_ID + "/stats", SC_NOT_FOUND);
  }

  private void createFields() throws IOException, URISyntaxException {
    CustomField postField2 = readJsonFile("fields/post/postCustomField2.json", CustomField.class);
    CustomField postField = readJsonFile("fields/post/postCustomField.json", CustomField.class);
    saveCustomField(STUB_FIELD_ID, postField, vertx);
    saveCustomField(STUB_FIELD_ID_2, postField2, vertx);
  }
}
