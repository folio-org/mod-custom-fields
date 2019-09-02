package org.folio.service;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;

import java.text.Normalizer;

import io.vertx.core.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.folio.common.OkapiParams;
import org.folio.repository.CustomFieldsRepository;
import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldCollection;
import org.folio.service.exc.ServiceExceptions;

@Component
public class CustomFieldsServiceImpl implements CustomFieldsService {

  @Autowired
  private CustomFieldsRepository repository;
  @Autowired
  private UserService userService;

  @Override
  public Future<CustomField> save(CustomField customField, OkapiParams params) {
    final String unAccentName = unAccentName(customField.getName());

    return populateCreator(customField, params)
      .compose(o -> repository.countById(unAccentName, params.getTenant()))
      .compose(count -> {
        customField.setId(getCustomFieldId(unAccentName, count));
        return repository.save(customField, params.getTenant());
      });
  }

  @Override
  public Future<Void> update(String id, CustomField customField, OkapiParams params) {
    customField.setId(id);
    return populateUpdater(customField, params)
      .compose(o -> repository.update(customField, params.getTenant()))
      .compose(found -> found ? succeededFuture() : failedFuture(ServiceExceptions.notFound(CustomField.class, id)));
  }

  @Override
  public Future<CustomField> findById(String id, String tenantId) {
    return repository.findById(id, tenantId)
      .map(customField -> customField.orElseThrow(() -> ServiceExceptions.notFound(CustomField.class, id)));
  }

  @Override
  public Future<CustomFieldCollection> findByQuery(String query, int offset, int limit, String lang, String tenantId) {
    return repository.findByQuery(query, offset, limit, tenantId);
  }

  private Future<Void> populateCreator(CustomField entity, OkapiParams params) {
    return userService.getUserInfo(params.getHeadersAsMap()).map(user -> {
      if (entity.getMetadata() != null) {
        entity.getMetadata().setCreatedByUsername(user.getUsername());
      }
      return null;
    });
  }

  private Future<Void> populateUpdater(CustomField entity, OkapiParams params) {
    return userService.getUserInfo(params.getHeadersAsMap()).map(user -> {
      if (entity.getMetadata() != null) {
        entity.getMetadata().setUpdatedByUsername(user.getUsername());
      }
      return null;
    });
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
