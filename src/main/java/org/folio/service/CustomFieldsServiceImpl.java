package org.folio.service;

import java.text.Normalizer;

import io.vertx.core.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.folio.repository.CustomFieldsRepository;
import org.folio.rest.jaxrs.model.CustomField;
import org.folio.service.exc.ServiceExceptions;

@Component
public class CustomFieldsServiceImpl implements CustomFieldsService {

  @Autowired
  private CustomFieldsRepository repository;

  @Override
  public Future<CustomField> save(CustomField customField, String tenantId) {
    final String customFieldId = unAccentName(customField.getName());

    return repository.countById(customFieldId, tenantId)
      .compose(count -> {
        customField.setId(getCustomFieldId(customFieldId, count));
        return repository.save(customField, tenantId).map(saved -> saved.orElse(null));
      });
  }

  @Override
  public Future<CustomField> findById(String id, String tenantId) {
    return repository.findById(id, tenantId)
      .map(customField -> customField.orElseThrow(() -> ServiceExceptions.notFound(CustomField.class, id)));
  }

  private String unAccentName(String customFieldName) {
    return Normalizer.normalize(customFieldName, Normalizer.Form.NFD)
      .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
      .replaceAll("[^a-zA-Z\\s]", "")
      .replaceAll("\\s+", "-").toLowerCase();
  }

  private String getCustomFieldId(String id, Integer count) {
    id = id.length() >= 65 ? id.substring(0, 65) : id;
    return id + "_" + (count + 1);
  }




}
