package org.folio.validate;

import io.vertx.core.json.Json;

public class ValidationTestUtil {
  public static Object parseCustomFieldJsonValue(String jsonValue){
    CustomFieldValue value = Json.decodeValue("{\"custom-field-ref-id\" : " + jsonValue + "}", CustomFieldValue.class);
    return value.getAdditionalProperties().get("custom-field-ref-id");
  }
}
