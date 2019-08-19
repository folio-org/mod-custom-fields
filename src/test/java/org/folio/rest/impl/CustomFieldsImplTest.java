package org.folio.rest.impl;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_IMPLEMENTED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.folio.test.util.TestUtil.STUB_TENANT;
import static org.folio.test.util.TestUtil.readJsonFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import static org.folio.test.util.TestUtil.readFile;

import java.io.IOException;
import java.net.URISyntaxException;

import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.http.HttpStatus;
import org.folio.rest.jaxrs.model.CustomFieldCollection;
import org.folio.test.util.TestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.folio.rest.jaxrs.model.CustomField;

@RunWith(VertxUnitRunner.class)
public class CustomFieldsImplTest extends TestBase {

  private static final String FIELD_ID = "department";
  private static final String FIELD_ID_2 = "expiration-date";

  private static final String CUSTOM_FIELDS_PATH = "custom-fields";

  private static final String CUSTOM_FIELDS_ID_PATH = CUSTOM_FIELDS_PATH + "/" + FIELD_ID;


  @Before
  public void setUp() {
    CustomFieldsDBTestUtil.deleteAllCustomFields(vertx);
  }

  @Test
  public void postCustomFields() throws IOException, URISyntaxException {
    final String cfWithAccentName = readFile("fields/post/postCustomFieldWithAccentName.json");
    final CustomField customField = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED)
      .as(CustomField.class);

    assertEquals("this-is-a-tricky-string_1", customField.getId());
  }

  @Test
  public void shouldCreateTwoRefIdOnPostCustomFieldWithSameName() throws IOException, URISyntaxException {
    final String cfWithAccentName = readFile("fields/post/postCustomFieldWithAccentName.json");
    final CustomField customField_one = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED)
      .as(CustomField.class);

    final CustomField customField_two = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED)
      .as(CustomField.class);
    assertEquals("this-is-a-tricky-string_1", customField_one.getId());
    assertEquals("this-is-a-tricky-string_2", customField_two.getId());
  }

  @Test
  public void shouldCreateTwoRefIdOnPostCustomFieldWithSameName2() throws IOException, URISyntaxException {
    final String cfWithHalfName = readFile("fields/post/postCustomFieldHalfName.json");
    final String cfWithAccentName = readFile("fields/post/postCustomFieldWithAccentName.json");
    final CustomField customField_one = postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_CREATED)
      .as(CustomField.class);

    final CustomField customField_two = postWithStatus(CUSTOM_FIELDS_PATH, cfWithAccentName, SC_CREATED)
      .as(CustomField.class);
    assertEquals("this-is-a_1", customField_one.getId());
    assertEquals("this-is-a-tricky-string_1", customField_two.getId());
  }

  @Test
  public void shouldNotCreateCustomFieldWhenNameIsTooLong() throws IOException, URISyntaxException {
    final String cfWithHalfName = readFile("fields/post/postCustomNameWithTooLongName.json");
    postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_CREATED);
  }

  @Test
  public void shouldReturn422WhenNameIsEmpty() throws IOException, URISyntaxException {
    final String cfWithHalfName = readFile("fields/post/postCustomFieldEmptyName.json");
    postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_UNPROCESSABLE_ENTITY);
  }

  @Test
  public void shouldReturn422WhenTypeIsEmpty() throws IOException, URISyntaxException {
    final String cfWithHalfName = readFile("fields/post/postCustomFieldEmptyType.json");
    postWithStatus(CUSTOM_FIELDS_PATH, cfWithHalfName, SC_UNPROCESSABLE_ENTITY);
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
    getWithStatus(CUSTOM_FIELDS_PATH + "?limit=-1", HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  public void shouldReturn400OnInvalidQuery() {
  getWithStatus(CUSTOM_FIELDS_PATH + "?query=name~~abc", HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  public void shouldReturnFieldOnValidId() throws IOException, URISyntaxException {
    createFields();

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

  public void getCustomFieldsById() throws IOException, URISyntaxException {
    final String postField = readFile("fields/postCustomField.json");

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
  public void deleteCustomFieldsById() {
    deleteWithStatus(CUSTOM_FIELDS_ID_PATH, SC_NOT_IMPLEMENTED);
  }

  @Test
  public void putCustomFieldsById() {
    putWithStatus(CUSTOM_FIELDS_ID_PATH, "{\"name\": \"test\", \"type\": \"SINGLE_CHECKBOX\"}", SC_NOT_IMPLEMENTED);
  }

  private void createFields() throws IOException, URISyntaxException {
    CustomField postField = readJsonFile("fields/post/postCustomField.json", CustomField.class);
    CustomField postField2 = readJsonFile("fields/post/postCustomField2.json", CustomField.class);
    CustomFieldsDBTestUtil.saveCustomField(FIELD_ID, postField, vertx);
    CustomFieldsDBTestUtil.saveCustomField(FIELD_ID_2, postField2, vertx);
  }
}
