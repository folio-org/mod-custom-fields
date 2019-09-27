package org.folio.validate;

import static org.folio.validate.ValidationUtil.createError;

import java.util.Collections;
import java.util.List;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.Error;
import org.folio.rest.jaxrs.model.Parameter;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class RadioButtonValidator implements CustomFieldValidator {
  @Override
  public List<Error> validate(Object fieldValue, CustomField fieldDefinition) {
    if(!(fieldValue instanceof String)){
      String jsonString = new Gson().toJson(fieldValue);
      return Collections.singletonList(
        createError(jsonString, fieldDefinition.getRefId(), "Radio button must be a string"));
    }

    String stringValue = (String) fieldValue;

    List<String> possibleValues = fieldDefinition.getSelectField().getOptions().getValues();
    if(!possibleValues.contains(stringValue)){
      return Collections.singletonList(
        createError(stringValue, fieldDefinition.getRefId(), "Field " + fieldDefinition.getRefId() +
          " can only have following values " + possibleValues));
    }

    return Collections.emptyList();
  }
  @Override
  public CustomField.Type supportedType() {
    return CustomField.Type.RADIO_BUTTON;
  }
}
