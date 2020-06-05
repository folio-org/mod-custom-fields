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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import static org.folio.CustomFieldsTestUtil.itemResourcePath;
import static org.folio.CustomFieldsTestUtil.itemStatResourcePath;
import static org.folio.CustomFieldsTestUtil.mockUserRequests;
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
    CustomField customField = createCustomField(readFile("fields/post/singleSelect/postValidSingleSelect.json"));

    assertEquals("favoritefood_1", customField.getRefId());
    assertThat(customField.getSelectField().getOptions().getValues(),
      hasItem(hasProperty("id", equalTo("opt_2")))
    );

    Metadata noteTypeMetadata = customField.getMetadata();

    assertEquals(USER1_ID, noteTypeMetadata.getCreatedByUserId());
    assertEquals("u1", noteTypeMetadata.getCreatedByUsername());
  }

  @Test
  public void shouldCreateTwoRefIdOnPostCustomFieldWithSameName() throws IOException, URISyntaxException {
    CustomField cfWithAccentName1 = createCustomField(readFile("fields/post/postCustomFieldWithAccentName.json"));
    CustomField cfWithAccentName2 = createCustomField(readFile("fields/post/postCustomFieldWithAccentNameSecond.json"));
    assertEquals("this-is-a-tricky-string_1", cfWithAccentName1.getRefId());
    assertEquals("this-is-a-tricky-string_2", cfWithAccentName2.getRefId());
  }

  @Test
  public void shouldCreateTwoRefIdOnPostCustomFieldWithSameName2() throws IOException, URISyntaxException {
    CustomField cfWithAccentName = createCustomField(readFile("fields/post/postCustomFieldWithAccentName.json"));
    CustomField cfWithHalfName = createCustomField(readFile("fields/post/postCustomFieldHalfName2.json"));
    assertEquals("this-is-a-tricky-string_1", cfWithAccentName.getRefId());
    assertEquals("this-is-a_1", cfWithHalfName.getRefId());
  }

  @Test
  public void shouldCreateCustomFieldWithLastOrderOnPost() throws IOException, URISyntaxException {
    CustomField customField1 = createCustomField(readFile("fields/post/postCustomField.json"));
    CustomField customField2 = createCustomField(readFile("fields/post/postCustomField2.json"));
    assertEquals(1, (int) customField1.getOrder());
    assertEquals(2, (int) customField2.getOrder());
  }

  @Test
  public void shouldNotCreateCustomFieldWhenNameIsTooLongOnPost() throws IOException, URISyntaxException {
    String customField = readFile("fields/post/postCustomNameWithTooLongName.json");
    Error error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY, USER1_HEADER).as(Error.class);
    assertThat(error.getMessage(), containsString("The 'name' length cannot be more than"));
  }

  @Test
  public void shouldReturn422WhenNameIsNullOnPost() throws IOException, URISyntaxException {
    String customField = readFile("fields/post/postCustomFieldEmptyName.json");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY, USER1_HEADER).asString();
    assertThat(error, containsString("may not be null"));
  }

  @Test
  public void shouldReturn422WhenTypeIsNullOnPost() throws IOException, URISyntaxException {
    String customField = readFile("fields/post/postCustomFieldEmptyType.json");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY).asString();
    assertThat(error, containsString("may not be null"));
  }

  @Test
  public void shouldReturn422WhenEntityTypeIsNullOnPost() throws IOException, URISyntaxException {
    String customField = readFile("fields/post/postCustomFieldEmptyEntityType.json");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY).asString();
    assertThat(error, containsString("may not be null"));
  }

  @Test
  public void shouldReturnErrorIfHelpTextTooLong() throws IOException, URISyntaxException {
    String customField = readFile("fields/post/postCustomFieldHelpTextInvalid.json");
    Error error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNPROCESSABLE_ENTITY).as(Error.class);
    assertThat(error.getMessage(), containsString("The 'helpText' length cannot be more than"));
  }

  @Test
  public void shouldReturnErrorIfInvalidCustomFieldType() throws IOException, URISyntaxException {
    String customField = readFile("fields/post/postInvalidCustomField.json");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_BAD_REQUEST).asString();
    assertThat(error, containsString("Json content error"));
  }

  @Test
  public void shouldReturn401WhenNoTokenHeader() throws IOException, URISyntaxException {
    String customField = readFile("fields/post/postCustomFieldHalfName.json");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, customField, SC_UNAUTHORIZED).asString();
    assertThat(error, containsString("Unauthorized"));
  }

  @Test
  public void shouldReturn422IfOptionIdIsInvalid() throws IOException, URISyntaxException {
    CustomField customField = readJsonFile("fields/post/singleSelect/postValidSingleSelect.json", CustomField.class);
    customField.getSelectField().getOptions().getValues().get(0).setId("opt_12121212121221");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, Json.encode(customField), SC_UNPROCESSABLE_ENTITY).asString();
    assertThat(error, containsString("must match \\\"opt_\\\\d{1,5}\\\""));
  }

  @Test
  public void shouldReturn401WhenUserUnauthorized() throws IOException, URISyntaxException {
    Header userWithoutPermission = new Header(XOkapiHeaders.USER_ID, USER3_ID);
    String cfWithHalfName = readFile("fields/post/postCustomFieldHalfName.json");
    String error = postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_UNAUTHORIZED, userWithoutPermission).asString();
    assertThat(error, containsString("Unauthorized"));
  }

  @Test
  public void shouldReturn404WhenUserNotFound() throws IOException, URISyntaxException {
    Header userWithoutPermission = createTokenHeader("name", USER4_ID);
    String cfWithHalfName = readFile("fields/post/postCustomFieldHalfName.json");
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
    String resourcePath = CUSTOM_FIELDS_PATH + "?query=name==Department";
    CustomFieldCollection fields = getWithOk(resourcePath).as(CustomFieldCollection.class);
    assertEquals(1, fields.getCustomFields().size());
    assertEquals(1, (int) fields.getTotalRecords());
    assertEquals("Department", fields.getCustomFields().get(0).getName());
    assertEquals("Provide a department", fields.getCustomFields().get(0).getHelpText());
    assertEquals("user", fields.getCustomFields().get(0).getEntityType());
  }

  @Test
  public void shouldReturnFieldsByEntityType() throws IOException, URISyntaxException {
    createFields();
    String resourcePath = CUSTOM_FIELDS_PATH + "?query=entityType==package";
    CustomFieldCollection fields = getWithOk(resourcePath).as(CustomFieldCollection.class);
    assertEquals(1, fields.getCustomFields().size());
    assertEquals(1, (int) fields.getTotalRecords());
    assertEquals("Expiration Date", fields.getCustomFields().get(0).getName());
    assertEquals("Set expiration date", fields.getCustomFields().get(0).getHelpText());
  }

  @Test
  public void shouldReturnFieldsWithPagination() throws IOException, URISyntaxException {
    createFields();
    String resourcePath = CUSTOM_FIELDS_PATH + "?offset=0&limit=1&query=cql.allRecords=1 sortby name";
    CustomFieldCollection fields = getWithOk(resourcePath).as(CustomFieldCollection.class);
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
    CustomField customField = createCustomField(readFile("fields/post/postCustomField.json"));

    CustomField actual = getWithOk(itemResourcePath(customField.getId())).as(CustomField.class);

    assertEquals("Department", actual.getName());
    assertEquals("department_1", actual.getRefId());
    assertEquals("Provide a department", actual.getHelpText());
    assertEquals(true, actual.getRequired());
    assertEquals(true, actual.getVisible());
    assertEquals(CustomField.Type.SINGLE_CHECKBOX, actual.getType());
  }

  @Test
  public void shouldReturn404OnMissingId() {
    String error = getWithStatus(itemResourcePath(STUB_FIELD_ID), SC_NOT_FOUND).asString();
    assertThat(error, containsString("CustomField not found by id"));
  }

  @Test
  public void getCustomFieldsById() throws IOException, URISyntaxException {
    CustomField customField = createCustomField(readFile("fields/post/postCustomField.json"));

    CustomField actual = getWithOk(itemResourcePath(customField.getId())).as(CustomField.class);

    assertEquals("Department", actual.getName());
    assertNotNull(actual.getRefId());
    assertEquals("Provide a department", actual.getHelpText());
    assertEquals(true, actual.getRequired());
    assertEquals(true, actual.getVisible());
    assertEquals(CustomField.Type.SINGLE_CHECKBOX, actual.getType());
  }

  @Test
  public void shouldUpdateCustomFieldNameAndRemoveOneOptionOnPut() throws IOException, URISyntaxException {
    CustomField customField = createCustomField(readFile("fields/post/radioButton/postValidRadioButton.json"));
    customField.setName("favoriteFood 2");
    customField.getSelectField().getOptions().getValues().remove(0);
    putWithNoContent(itemResourcePath(customField.getId()), Json.encode(customField), USER2_HEADER);

    CustomField actual = getAllCustomFields(vertx).get(0);
    assertEquals("favoriteFood 2", actual.getName());
    assertEquals("favoritefood_1", actual.getRefId());
    assertEquals("Choose your favorite food", actual.getHelpText());
    assertEquals(true, actual.getRequired());
    assertEquals(true, actual.getVisible());
    assertEquals(1, (int) actual.getOrder());
    assertEquals(CustomField.Type.RADIO_BUTTON, actual.getType());
    assertThat(actual.getSelectField().getOptions().getValues(), hasSize(2));

    Metadata noteTypeMetadata = actual.getMetadata();
    assertEquals(USER1_ID, noteTypeMetadata.getCreatedByUserId());
    assertEquals("u1", noteTypeMetadata.getCreatedByUsername());
    assertEquals(USER2_ID, noteTypeMetadata.getUpdatedByUserId());
    assertEquals("u2", noteTypeMetadata.getUpdatedByUsername());
  }

  @Test
  public void shouldNotChangeRefIdWhenNameIsSameOnPut() throws IOException, URISyntaxException {
    CustomField customField = createCustomField(readFile("fields/post/postCustomField2.json"));
    String resourcePath = itemResourcePath(customField.getId());
    putWithNoContent(resourcePath, readFile("fields/put/putCustomField2.json"), USER2_HEADER);

    CustomField field = getAllCustomFields(vertx).get(0);
    assertEquals("Expiration Date", field.getName());
    assertEquals("expiration-date_1", field.getRefId());
    assertEquals("Set expiration date", field.getHelpText());
    assertEquals(true, field.getRequired());
    assertEquals(true, field.getVisible());
    assertEquals(CustomField.Type.TEXTBOX_SHORT, field.getType());

    Metadata noteTypeMetadata = field.getMetadata();

    assertEquals(USER1_ID, noteTypeMetadata.getCreatedByUserId());
    assertEquals("u1", noteTypeMetadata.getCreatedByUsername());

    assertEquals(USER2_ID, noteTypeMetadata.getUpdatedByUserId());
    assertEquals("u2", noteTypeMetadata.getUpdatedByUsername());
  }

  @Test
  public void shouldReturn422WhenCustomFieldTypeChangedOnPut() throws IOException, URISyntaxException {
    CustomField customField = createCustomField(readFile("fields/post/postCustomField.json"));
    String resourcePath = itemResourcePath(customField.getId());
    String putBody = readFile("fields/put/putCustomField2.json");
    Error error = putWithStatus(resourcePath, putBody, SC_UNPROCESSABLE_ENTITY, USER2_HEADER).as(Error.class);
    assertThat(error.getMessage(), containsString("The type of the custom field can not be changed"));
  }

  @Test
  public void shouldReturn422WhenNameIsNullOnPut() throws IOException, URISyntaxException {
    String postBody = readFile("fields/post/postCustomField.json");
    String putBody = readFile("fields/put/putCustomFieldEmptyName.json");

    CustomField customField = createCustomField(postBody);
    String resourcePath = itemResourcePath(customField.getId());
    String error = putWithStatus(resourcePath, putBody, SC_UNPROCESSABLE_ENTITY, USER1_HEADER).asString();
    assertThat(error, containsString("may not be null"));
  }

  @Test
  public void shouldReturn422WhenTypeIsNullOnPut() throws IOException, URISyntaxException {
    String postBody = readFile("fields/post/postCustomField.json");
    String putBody = readFile("fields/put/putCustomFieldEmptyType.json");

    CustomField customField = createCustomField(postBody);
    String resourcePath = itemResourcePath(customField.getId());
    String error = putWithStatus(resourcePath, putBody, SC_UNPROCESSABLE_ENTITY).asString();
    assertThat(error, containsString("may not be null"));
  }

  @Test
  public void shouldReturn422WhenEntityTypeIsNullOnPut() throws IOException, URISyntaxException {
    String postBody = readFile("fields/post/postCustomField.json");
    String putBody = readFile("fields/put/putCustomFieldEmptyEntityType.json");

    CustomField customField = createCustomField(postBody);
    String resourcePath = itemResourcePath(customField.getId());
    String error = putWithStatus(resourcePath, putBody, SC_UNPROCESSABLE_ENTITY).asString();
    assertThat(error, containsString("may not be null"));
  }

  @Test
  public void shouldUpdateAllCustomFields() throws IOException, URISyntaxException {
    CustomField createdField = createCustomField(readFile("fields/post/postCustomField.json"));
    createCustomField(readFile("fields/post/postCustomField2.json"));

    CustomFieldCollection request = readJsonFile("fields/put/putCustomFieldCollection.json", CustomFieldCollection.class);
    request.getCustomFields().get(0).setId(createdField.getId());
    putWithNoContent(CUSTOM_FIELDS_PATH, Json.encode(request), USER2_HEADER);

    List<CustomField> customFields = getAllCustomFields(vertx);
    customFields.sort(Comparator.comparing(CustomField::getOrder));
    CustomField firstField = customFields.get(0);
    assertEquals("Department 2", firstField.getName());
    assertEquals("department_1", firstField.getRefId());
    assertEquals("Provide a second department", firstField.getHelpText());
    assertEquals(false, firstField.getRequired());
    assertEquals(false, firstField.getVisible());
    assertEquals(1, (int) firstField.getOrder());
    assertEquals(CustomField.Type.SINGLE_CHECKBOX, firstField.getType());
    Metadata firstFieldMetadata = firstField.getMetadata();
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
    Metadata secondFieldMetadata = secondField.getMetadata();
    assertEquals(USER2_ID, secondFieldMetadata.getCreatedByUserId());
    assertEquals("u2", secondFieldMetadata.getCreatedByUsername());
    assertEquals(USER2_ID, secondFieldMetadata.getUpdatedByUserId());
    assertEquals("u2", secondFieldMetadata.getUpdatedByUsername());
  }

  @Test
  public void deleteCustomFieldsById() throws IOException, URISyntaxException {
    String postBody = readFile("fields/post/postCustomField.json");
    CustomField customField = createCustomField(postBody);

    deleteWithNoContent(itemResourcePath(customField.getId()));
    String error = deleteWithStatus(itemResourcePath(STUB_FIELD_ID), SC_NOT_FOUND).asString();
    assertThat(error, containsString("CustomField not found by id"));
  }

  @Test
  public void deleteCustomFieldAndReorderLast() throws IOException, URISyntaxException {
    String postBody1 = readFile("fields/post/postCustomField.json");
    String postBody2 = readFile("fields/post/postCustomFieldOrder2.json");
    String postBody3 = readFile("fields/post/postCustomFieldOrder3.json");
    CustomField customFieldOne = createCustomField(postBody1);
    createCustomField(postBody2);
    createCustomField(postBody3);

    deleteWithNoContent(itemResourcePath(customFieldOne.getId()));

    CustomFieldCollection fields = getWithOk(CUSTOM_FIELDS_PATH).as(CustomFieldCollection.class);
    assertEquals(2, fields.getCustomFields().size());
    assertEquals(1, (int) fields.getCustomFields().get(0).getOrder());
    assertEquals(2, (int) fields.getCustomFields().get(1).getOrder());
  }

  @Test
  public void deleteCustomFieldAndReorderLast1212() throws IOException, URISyntaxException {
    createCustomField(readFile("fields/post/postCustomFieldOrder3.json"));
    createCustomField(readFile("fields/post/postCustomField.json"));
    createCustomField(readFile("fields/post/postCustomFieldOrder2.json"));

    deleteWithStatus(CUSTOM_FIELDS_PATH + "/11111111-2222-3333-a444-555555555555", SC_NOT_FOUND);

    String resourcePath = CUSTOM_FIELDS_PATH + "?query=cql.allRecords=1 sortby order";
    CustomFieldCollection fields = getWithOk(resourcePath).as(CustomFieldCollection.class);
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
    CustomField customFieldOne = createCustomField(readFile("fields/post/postCustomField.json"));

    createCustomField(readFile("fields/post/postCustomFieldOrder2.json"));

    String id = customFieldOne.getId();
    deleteWithNoContent(itemResourcePath(id));

    CustomField customFieldThree = createCustomField(readFile("fields/post/postCustomFieldOrder3.json"));

    assertTrue(customFieldThree.getRefId().endsWith("_3"));
  }

  @Test
  public void shouldReturnEmptyStatsForExistingField() throws IOException, URISyntaxException {
    CustomField field = createCustomField(readFile("fields/post/postCustomField.json"));

    String resourcePath = itemStatResourcePath(field.getId());
    CustomFieldStatistic stats = getWithOk(resourcePath).as(CustomFieldStatistic.class);

    assertEquals(stats, new CustomFieldStatistic()
      .withFieldId(field.getId())
      .withEntityType(field.getEntityType())
      .withCount(0));
  }

  @Test
  public void shouldFailWith404WhenStatsRequestedForNonExistingField() {
    String error = getWithStatus(itemStatResourcePath(STUB_FIELD_ID), SC_NOT_FOUND).asString();
    assertThat(error, containsString("CustomField not found by id"));
  }

  private void createFields() throws IOException, URISyntaxException {
    createCustomField(readFile("fields/post/postCustomField.json"));
    createCustomField(readFile("fields/post/postCustomField2.json"));
  }

  private CustomField createCustomField(String postBody) {
    return postWithStatus(CUSTOM_FIELDS_PATH, postBody, SC_CREATED, USER1_HEADER).as(CustomField.class);
  }
}
