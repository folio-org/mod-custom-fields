package org.folio.rest.impl;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.folio.CustomFieldsTestUtil.CUSTOM_FIELDS_ID_PATH;
import static org.folio.CustomFieldsTestUtil.CUSTOM_FIELDS_PATH;
import static org.folio.CustomFieldsTestUtil.STUB_FIELD_ID;
import static org.folio.CustomFieldsTestUtil.USER1_HEADER;
import static org.folio.CustomFieldsTestUtil.USER1_ID;
import static org.folio.CustomFieldsTestUtil.USER2_HEADER;
import static org.folio.CustomFieldsTestUtil.USER2_ID;
import static org.folio.CustomFieldsTestUtil.USER3_ID;
import static org.folio.CustomFieldsTestUtil.USER4_ID;
import static org.folio.CustomFieldsTestUtil.deleteAllCustomFields;
import static org.folio.CustomFieldsTestUtil.getAllCustomFields;
import static org.folio.CustomFieldsTestUtil.mockUserRequests;
import static org.folio.CustomFieldsTestUtil.saveCustomField;
import static org.folio.test.util.TestUtil.readFile;
import static org.folio.test.util.TestUtil.readJsonFile;
import static org.folio.test.util.TokenTestUtil.createTokenHeader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;

import io.restassured.http.Header;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.folio.okapi.common.XOkapiHeaders;
import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldCollection;
import org.folio.rest.jaxrs.model.CustomFieldStatistic;
import org.folio.rest.jaxrs.model.Error;
import org.folio.rest.jaxrs.model.Metadata;
import org.folio.test.util.TestBase;

@RunWith(VertxUnitRunner.class)
public class CustomFieldsImplTest extends TestBase {

  @Before
  public void setUp() throws IOException, URISyntaxException {
    deleteAllCustomFields(vertx);
    mockUserRequests();
  }

  @Test
  public void postCustomFields() throws IOException, URISyntaxException {
    final String cfWithAccentName = readFile("fields/post/singleSelect/postValidSingleSelect.json");
    final CustomField customField = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED, USER1_HEADER)
      .as(CustomField.class);

    assertEquals("favoritefood_1", customField.getRefId());
    assertThat(customField.getSelectField().getOptions().getValues(),
      hasItem(hasProperty("id", equalTo("opt_2")))
    );

    final Metadata noteTypeMetadata = customField.getMetadata();

    assertEquals(USER1_ID, noteTypeMetadata.getCreatedByUserId());
    assertEquals("u1", noteTypeMetadata.getCreatedByUsername());
  }

  @Test
  public void shouldCreateTwoRefIdOnPostCustomFieldWithSameName() throws IOException, URISyntaxException {
    final String cfWithAccentName = readFile("fields/post/postCustomFieldWithAccentName.json");
    final CustomField customField_one = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED, USER1_HEADER)
      .as(CustomField.class);
    final String cfWithAccentName2 = readFile("fields/post/postCustomFieldWithAccentNameSecond.json");
    final CustomField customField_two = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName2, SC_CREATED, USER1_HEADER)
      .as(CustomField.class);
    assertEquals("this-is-a-tricky-string_1", customField_one.getRefId());
    assertEquals("this-is-a-tricky-string_2", customField_two.getRefId());
  }

  @Test
  public void shouldCreateTwoRefIdOnPostCustomFieldWithSameName2() throws IOException, URISyntaxException {
    final String cfWithAccentName = readFile("fields/post/postCustomFieldWithAccentName.json");
    final String cfWithHalfName = readFile("fields/post/postCustomFieldHalfName2.json");
    final CustomField customField_one = postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_CREATED, USER1_HEADER)
      .as(CustomField.class);

    final CustomField customField_two = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED, USER1_HEADER)
      .as(CustomField.class);
    assertEquals("this-is-a_1", customField_one.getRefId());
    assertEquals("this-is-a-tricky-string_1", customField_two.getRefId());
  }

  @Test
  public void shouldCreateCustomFieldWithLastOrderOnPost() throws IOException, URISyntaxException {
    final CustomField customField1 = postWithStatus(CUSTOM_FIELDS_PATH,
      readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER)
      .as(CustomField.class);
    final CustomField customField2 = postWithStatus(CUSTOM_FIELDS_PATH,
      readFile("fields/post/postCustomField2.json"), SC_CREATED, USER1_HEADER)
      .as(CustomField.class);
    assertEquals(1, (int) customField1.getOrder());
    assertEquals(2, (int) customField2.getOrder());
  }

  @Test
  public void shouldNotCreateCustomFieldWhenNameIsTooLongOnPost() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postCustomNameWithTooLongName.json");
    Error error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY, USER1_HEADER).as(Error.class);

    assertThat(error.getMessage(), containsString("The 'name' length cannot be more than"));
  }

  @Test
  public void shouldReturn422WhenNameIsNullOnPost() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postCustomFieldEmptyName.json");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY, USER1_HEADER).asString();
    assertThat(error, containsString("may not be null"));
  }

  @Test
  public void shouldReturn422WhenTypeIsNullOnPost() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postCustomFieldEmptyType.json");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY).asString();
    assertThat(error, containsString("may not be null"));
  }

  @Test
  public void shouldReturn422WhenEntityTypeIsNullOnPost() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postCustomFieldEmptyEntityType.json");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY).asString();
    assertThat(error, containsString("may not be null"));
  }

  @Test
  public void shouldReturnErrorIfHelpTextTooLong() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postCustomFieldHelpTextInvalid.json");
    Error error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY).as(Error.class);
    assertThat(error.getMessage(), containsString("The 'helpText' length cannot be more than"));
  }

  @Test
  public void shouldReturnErrorIfInvalidCustomFieldType() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postInvalidCustomField.json");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_BAD_REQUEST).asString();
    assertThat(error, containsString("Json content error"));
  }

  @Test
  public void shouldReturn401WhenNoTokenHeader() throws IOException, URISyntaxException {
    final String customField = readFile("fields/post/postCustomFieldHalfName.json");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNAUTHORIZED).asString();
    assertThat(error, containsString("Unauthorized"));
  }

  @Test
  public void shouldReturn401WhenUserUnauthorized() throws IOException, URISyntaxException {
    final Header userWithoutPermission = new Header(XOkapiHeaders.USER_ID, USER3_ID);
    final String cfWithHalfName = readFile("fields/post/postCustomFieldHalfName.json");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_UNAUTHORIZED, userWithoutPermission).asString();
    assertThat(error, containsString("Unauthorized"));
  }

  @Test
  public void shouldReturn404WhenUserNotFound() throws IOException, URISyntaxException {
    final Header userWithoutPermission = createTokenHeader("name", USER4_ID);
    final String cfWithHalfName = readFile("fields/post/postCustomFieldHalfName.json");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_NOT_FOUND, userWithoutPermission).asString();
    assertThat(error, containsString("User not found"));
  }

  @Test
  public void shouldReturnAllFieldsOnGetSortedByOrder() throws IOException, URISyntaxException {
    createFields();
    CustomFieldCollection fields = getWithOk(CUSTOM_FIELDS_PATH).as(CustomFieldCollection.class);
    assertEquals(2, fields.getCustomFields().size());
    assertThat(fields.getCustomFields().get(0), is(allOf(
      hasProperty("name", is("Department")),
      hasProperty("helpText", is("Provide a department")),
      hasProperty("entityType", is("user")),
      hasProperty("order", is(1))
    )));
    assertThat(fields.getCustomFields().get(1), is(allOf(
      hasProperty("name", is("Expiration Date")),
      hasProperty("helpText", is("Set expiration date")),
      hasProperty("entityType", is("package")),
      hasProperty("order", is(2))
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
    String error = getWithStatus(CUSTOM_FIELDS_PATH + "?limit=-1", SC_BAD_REQUEST).asString();
    assertThat(error, containsString("'limit' parameter is incorrect"));
  }

  @Test
  public void shouldReturn400OnInvalidQuery() {
    String error = getWithStatus(CUSTOM_FIELDS_PATH + "?query=name~~abc", SC_BAD_REQUEST).asString();
    assertThat(error, containsString("no serverChoiceIndexes defined"));
  }

  @Test
  public void shouldReturnFieldOnValidId() throws IOException, URISyntaxException {
    final CustomField customField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER)
        .as(CustomField.class);

    CustomField field = getWithOk(CUSTOM_FIELDS_PATH + "/" + customField.getId()).as(CustomField.class);

    assertEquals("Department", field.getName());
    assertEquals("department_1", field.getRefId());
    assertEquals("Provide a department", field.getHelpText());
    assertEquals(true, field.getRequired());
    assertEquals(true, field.getVisible());
    assertEquals(CustomField.Type.SINGLE_CHECKBOX, field.getType());
  }

  @Test
  public void shouldReturn404OnMissingId() {
    String error = getWithStatus(CUSTOM_FIELDS_ID_PATH, SC_NOT_FOUND).asString();
    assertThat(error, containsString("CustomField not found by id"));
  }

  @Test
  public void getCustomFieldsById() throws IOException, URISyntaxException {
    final CustomField postField = readJsonFile("fields/post/postCustomField.json", CustomField.class);

    saveCustomField(STUB_FIELD_ID, postField, vertx);

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
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER)
        .as(CustomField.class);
    putWithNoContent(CUSTOM_FIELDS_PATH + "/" + customField.getId(), readFile("fields/put/putCustomField.json"),
      USER2_HEADER);

    CustomField field = getAllCustomFields(vertx).get(0);
    assertEquals("Department 2", field.getName());
    assertEquals("department_1", field.getRefId());
    assertEquals("Provide a second department", field.getHelpText());
    assertEquals(false, field.getRequired());
    assertEquals(false, field.getVisible());
    assertEquals(1, (int) field.getOrder());
    assertEquals(CustomField.Type.SINGLE_CHECKBOX, field.getType());

    final Metadata noteTypeMetadata = field.getMetadata();

    assertEquals(USER1_ID, noteTypeMetadata.getCreatedByUserId());
    assertEquals("u1", noteTypeMetadata.getCreatedByUsername());

    assertEquals(USER2_ID, noteTypeMetadata.getUpdatedByUserId());
    assertEquals("u2", noteTypeMetadata.getUpdatedByUsername());
  }

  @Test
  public void shouldNotChangeRefIdWhenNameIsSameOnPut() throws IOException, URISyntaxException {
    final CustomField customField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField2.json"), SC_CREATED, USER1_HEADER)
        .as(CustomField.class);
    putWithNoContent(CUSTOM_FIELDS_PATH + "/" + customField.getId(), readFile("fields/put/putCustomField2.json"),
      USER2_HEADER);

    CustomField field = getAllCustomFields(vertx).get(0);
    assertEquals("Expiration Date", field.getName());
    assertEquals("expiration-date_1", field.getRefId());
    assertEquals("Set expiration date", field.getHelpText());
    assertEquals(true, field.getRequired());
    assertEquals(true, field.getVisible());
    assertEquals(CustomField.Type.TEXTBOX_SHORT, field.getType());

    final Metadata noteTypeMetadata = field.getMetadata();

    assertEquals(USER1_ID, noteTypeMetadata.getCreatedByUserId());
    assertEquals("u1", noteTypeMetadata.getCreatedByUsername());

    assertEquals(USER2_ID, noteTypeMetadata.getUpdatedByUserId());
    assertEquals("u2", noteTypeMetadata.getUpdatedByUsername());
  }

  @Test
  public void shouldReturn422WhenCustomFieldTypeChangedOnPut() throws IOException, URISyntaxException {
    final CustomField customField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER)
        .as(CustomField.class);
    Error error = putWithStatus(CUSTOM_FIELDS_PATH + "/" + customField.getId(),
      readFile("fields/put/putCustomField2.json"), SC_UNPROCESSABLE_ENTITY, USER2_HEADER).as(Error.class);
    assertThat(error.getMessage(), containsString("The type of the custom field can not be changed"));
  }

  @Test
  public void shouldReturn422WhenNameIsNullOnPut() throws IOException, URISyntaxException {
    final CustomField customField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER)
        .as(CustomField.class);

    final String cfWithHalfName = readFile("fields/put/putCustomFieldEmptyName.json");
    String resourcePath = CUSTOM_FIELDS_PATH + "/" + customField.getId();
    String error = putWithStatus(resourcePath, cfWithHalfName, SC_UNPROCESSABLE_ENTITY, USER1_HEADER).asString();
    assertThat(error, containsString("may not be null"));
  }

  @Test
  public void shouldReturn422WhenTypeIsNullOnPut() throws IOException, URISyntaxException {
    final CustomField customField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER)
        .as(CustomField.class);

    final String cfWithHalfName = readFile("fields/put/putCustomFieldEmptyType.json");
    String resourcePath = CUSTOM_FIELDS_PATH + "/" + customField.getId();
    String error = putWithStatus(resourcePath, cfWithHalfName, SC_UNPROCESSABLE_ENTITY).asString();
    assertThat(error, containsString("may not be null"));
  }

  @Test
  public void shouldReturn422WhenEntityTypeIsNullOnPut() throws IOException, URISyntaxException {
    final CustomField customField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER)
        .as(CustomField.class);

    final String cfWithHalfName = readFile("fields/put/putCustomFieldEmptyEntityType.json");
    String resourcePath = CUSTOM_FIELDS_PATH + "/" + customField.getId();
    String error = putWithStatus(resourcePath, cfWithHalfName, SC_UNPROCESSABLE_ENTITY).asString();
    assertThat(error, containsString("may not be null"));
  }

  @Test
  public void shouldUpdateAllCustomFields() throws IOException, URISyntaxException {
    CustomField createdField =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER)
        .as(CustomField.class);
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField2.json"), SC_CREATED, USER1_HEADER)
      .as(CustomField.class);

    CustomFieldCollection request = readJsonFile("fields/put/putCustomFieldCollection.json", CustomFieldCollection.class);
    request.getCustomFields().get(0).setId(createdField.getId());
    putWithNoContent(CUSTOM_FIELDS_PATH, Json.encode(request), USER2_HEADER);

    List<CustomField> customFields = getAllCustomFields(vertx);
    customFields
      .sort(Comparator.comparing(CustomField::getOrder));
    CustomField firstField = customFields.get(0);
    assertEquals("Department 2", firstField.getName());
    assertEquals("department_1", firstField.getRefId());
    assertEquals("Provide a second department", firstField.getHelpText());
    assertEquals(false, firstField.getRequired());
    assertEquals(false, firstField.getVisible());
    assertEquals(1, (int) firstField.getOrder());
    assertEquals(CustomField.Type.SINGLE_CHECKBOX, firstField.getType());
    final Metadata firstFieldMetadata = firstField.getMetadata();
    assertEquals(USER1_ID, firstFieldMetadata.getCreatedByUserId());
    assertEquals("u1", firstFieldMetadata.getCreatedByUsername());
    assertEquals(USER2_ID, firstFieldMetadata.getUpdatedByUserId());
    assertEquals("u2", firstFieldMetadata.getUpdatedByUsername());

    CustomField secondField = customFields.get(1);
    assertEquals("New Expiration Date", secondField.getName());
    assertEquals("new-expiration-date_1", secondField.getRefId());
    assertEquals("Set new expiration date", secondField.getHelpText());
    assertEquals(true, secondField.getRequired());
    assertEquals(true, secondField.getVisible());
    assertEquals(2, (int) secondField.getOrder());
    assertEquals(CustomField.Type.TEXTBOX_SHORT, secondField.getType());
    final Metadata secondFieldMetadata = secondField.getMetadata();
    assertEquals(USER2_ID, secondFieldMetadata.getCreatedByUserId());
    assertEquals("u2", secondFieldMetadata.getCreatedByUsername());
    assertEquals(USER2_ID, secondFieldMetadata.getUpdatedByUserId());
    assertEquals("u2", secondFieldMetadata.getUpdatedByUsername());
  }

  @Test
  public void deleteCustomFieldsById() throws IOException, URISyntaxException {
    final CustomField customFieldOne =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER)
        .as(CustomField.class);

    deleteWithNoContent(CUSTOM_FIELDS_PATH + "/" + customFieldOne.getId());
    String error = deleteWithStatus(CUSTOM_FIELDS_ID_PATH, SC_NOT_FOUND).asString();
    assertThat(error, containsString("CustomField not found by id"));
  }

  @Test
  public void deleteCustomFieldAndReorderLast() throws IOException, URISyntaxException {
    final CustomField customFieldOne =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER)
        .as(CustomField.class);
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomFieldOrder2.json"), SC_CREATED, USER1_HEADER);
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomFieldOrder3.json"), SC_CREATED, USER1_HEADER);

    deleteWithNoContent(CUSTOM_FIELDS_PATH + "/" + customFieldOne.getId());

    CustomFieldCollection fields = getWithOk(CUSTOM_FIELDS_PATH).as(CustomFieldCollection.class);
    assertEquals(2, fields.getCustomFields().size());
    assertEquals(1, (int) fields.getCustomFields().get(0).getOrder());
    assertEquals(2, (int) fields.getCustomFields().get(1).getOrder());
  }

  @Test
  public void deleteCustomFieldAndReorderLast1212() throws IOException, URISyntaxException {
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomFieldOrder3.json"), SC_CREATED, USER1_HEADER);
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER);
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomFieldOrder2.json"), SC_CREATED, USER1_HEADER);

    deleteWithStatus(CUSTOM_FIELDS_PATH + "/11111111-2222-3333-a444-555555555555", SC_NOT_FOUND);

    CustomFieldCollection fields =
      getWithOk(CUSTOM_FIELDS_PATH + "?query=cql.allRecords=1 sortby order").as(CustomFieldCollection.class);
    assertEquals(3, fields.getCustomFields().size());
    assertEquals(1, (int) fields.getCustomFields().get(0).getOrder());
    assertEquals(2, (int) fields.getCustomFields().get(1).getOrder());
    assertEquals(3, (int) fields.getCustomFields().get(2).getOrder());
  }

  @Test
  public void shouldReturn404WhenDeleteRequestHasNotExistingUUID() {
    String resourcePath = CUSTOM_FIELDS_PATH + "/11111111-2222-3333-a444-555555555555";
    String error = deleteWithStatus(resourcePath, SC_NOT_FOUND).asString();
    assertThat(error, containsString("CustomField not found by id"));
  }

  @Test
  public void shouldReturn400WhenDeleteRequestHasInvalidUUID() {
    String resourcePath = CUSTOM_FIELDS_PATH + "/11111111-3-1111-333-111111111111";
    String error = deleteWithStatus(resourcePath, SC_BAD_REQUEST).asString();
    assertThat(error, containsString("'id' parameter is incorrect"));
  }

  @Test
  public void shouldDeleteAndGenerateNewRefId() throws IOException, URISyntaxException {
    final CustomField customFieldOne =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER)
        .as(CustomField.class);

    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomFieldOrder2.json"), SC_CREATED, USER1_HEADER);

    deleteWithNoContent(CUSTOM_FIELDS_PATH + "/" + customFieldOne.getId());

    final CustomField customFieldThree =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomFieldOrder3.json"), SC_CREATED, USER1_HEADER)
        .as(CustomField.class);

    assertTrue(customFieldThree.getRefId().endsWith("_3"));

  }

  @Test
  public void shouldReturnEmptyStatsForExistingField() throws IOException, URISyntaxException {
    final CustomField field =
      postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER)
        .as(CustomField.class);

    CustomFieldStatistic stats = getWithOk(
      CUSTOM_FIELDS_PATH + "/" + field.getId() + "/stats").as(CustomFieldStatistic.class);

    assertEquals(stats, new CustomFieldStatistic()
      .withFieldId(field.getId())
      .withEntityType(field.getEntityType())
      .withCount(0));
  }

  @Test
  public void shouldFailWith404WhenStatsRequestedForNonExistingField() {
    String error = getWithStatus(CUSTOM_FIELDS_PATH + "/" + STUB_FIELD_ID + "/stats", SC_NOT_FOUND).asString();
    assertThat(error, containsString("CustomField not found by id"));
  }

  private void createFields() throws IOException, URISyntaxException {
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField.json"), SC_CREATED, USER1_HEADER)
      .as(CustomField.class);
    postWithStatus(CUSTOM_FIELDS_PATH, readFile("fields/post/postCustomField2.json"), SC_CREATED, USER1_HEADER)
      .as(CustomField.class);
  }
}
