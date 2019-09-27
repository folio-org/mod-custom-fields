package org.folio.validate;

import java.util.Map;

import io.vertx.core.Future;

public interface ValidationService {
  /**
   * Validates values of custom fields
   * If validation fails then failed Future with CustomFieldValidationException is returned
   * @param customFieldsMap map of custom field parsed from json
   *
   */
  Future<Void> validateCustomFields(Map<String, Object> customFieldsMap, String tenantId);
}
