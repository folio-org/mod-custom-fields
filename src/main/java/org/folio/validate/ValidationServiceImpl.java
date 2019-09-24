package org.folio.validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.Error;
import org.folio.rest.jaxrs.model.Errors;
import org.folio.service.CustomFieldsService;
import org.folio.spring.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class ValidationServiceImpl implements ValidationService {
  @Autowired
  private List<CustomFieldValidator> validators;
  @Autowired
  private CustomFieldsService customFieldsService;

  public ValidationServiceImpl() {
    SpringContextUtil.autowireDependencies(this, Vertx.currentContext());
  }

  @Override
  public Future<Void> validateCustomFields(Map<String, Object> customFieldsMap, String tenantId) {
    return customFieldsService
      .findByQuery("cql.allRecords=1", 0, Integer.MAX_VALUE, null, tenantId)
      .map(fields -> {
        List<Error> errorList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : customFieldsMap.entrySet()) {
          CustomField customField = findCustomField(entry.getKey(), fields.getCustomFields());
          errorList.addAll(validate(entry.getValue(), customField));
        }
        if(!errorList.isEmpty()){
          Errors errors = new Errors();
          errors.setErrors(errorList);
          throw new CustomFieldValidationException(errors);
        }
        return null;
      });
  }

  private CustomField findCustomField(String key, List<CustomField> customFields) {
    return customFields.stream()
      .filter(field -> key.equals(field.getRefId()))
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("Custom field with refId " + key + " is not found"));
  }

  private List<Error> validate(Object fieldValue, CustomField fieldDefinition) {
    return validators.stream()
      .filter(validator -> validator.supportedType() == fieldDefinition.getType())
      .findFirst()
      .map(validator -> validator.validate(fieldValue, fieldDefinition))
      .orElse(Collections.emptyList());
  }
}
