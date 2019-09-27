package org.folio.validate;

import static org.folio.validate.ValidationUtil.createError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.Error;
import org.folio.rest.jaxrs.model.Errors;
import org.folio.service.CustomFieldsService;
import org.folio.spring.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

import io.vertx.core.Context;
import io.vertx.core.Future;

public class ValidationServiceImpl implements ValidationService {
  @Autowired
  private List<CustomFieldValidator> validators;
  @Autowired
  private CustomFieldsService customFieldsService;

  public ValidationServiceImpl(Context vertxContext) {
    SpringContextUtil.autowireDependencies(this, vertxContext);
  }

  @Override
  public Future<Void> validateCustomFields(Map<String, Object> customFieldsMap, String tenantId) {
    return customFieldsService
      .findByQuery(null, 0, Integer.MAX_VALUE, null, tenantId)
      .compose(fields -> {
        List<Error> errorList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : customFieldsMap.entrySet()) {
          String key = entry.getKey();
          Optional<CustomField> customField = findCustomField(key, fields.getCustomFields());
          if(customField.isPresent()) {
            errorList.addAll(validate(entry.getValue(), customField.get()));
          }else{
            errorList.add(
              ValidationUtil
                .createError(key, "customFields", "Custom field with refId " + key + " is not found"));
          }

        }
        if(!errorList.isEmpty()){
          Errors errors = new Errors();
          errors.setErrors(errorList);
          return Future.failedFuture(new CustomFieldValidationException(errors));
        }
        return Future.succeededFuture();
      });
  }

  private Optional<CustomField> findCustomField(String key, List<CustomField> customFields) {
    return customFields.stream()
      .filter(field -> key.equals(field.getRefId()))
      .findFirst();
  }

  private List<Error> validate(Object fieldValue, CustomField fieldDefinition) {
    return validators.stream()
      .filter(validator -> validator.supportedType() == fieldDefinition.getType())
      .findFirst()
      .map(validator -> validate(fieldValue, fieldDefinition, validator))
      .orElse(Collections.emptyList());
  }

  private List<Error> validate(Object fieldValue, CustomField fieldDefinition, CustomFieldValidator validator) {
    try {
      validator.validate(fieldValue, fieldDefinition);
    }
    catch (IllegalArgumentException ex){
      return Collections.singletonList(
        createError(new Gson().toJson(fieldValue), fieldDefinition.getRefId(), ex.getMessage()));
    }
    return Collections.emptyList();
  }
}
