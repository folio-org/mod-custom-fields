package org.folio.service;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;

import static org.folio.db.DbUtils.executeInTransactionWithVertxFuture;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.NotFoundException;

import com.google.common.collect.Sets;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.sql.SQLConnection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.z3950.zing.cql.CQLDefaultNodeVisitor;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLSortNode;
import org.z3950.zing.cql.ModifierSet;

import org.folio.common.OkapiParams;
import org.folio.repository.CustomFieldsRepository;
import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldCollection;
import org.folio.rest.jaxrs.model.CustomFieldStatistic;
import org.folio.service.exc.ServiceExceptions;


@Component
public class CustomFieldsServiceImpl implements CustomFieldsService {

  private static final String ALL_RECORDS_QUERY = "cql.allRecords=1";
  private static final String ORDER_ATTRIBUTE = "order";
  @Autowired
  private CustomFieldsRepository repository;
  @Autowired
  private UserService userService;
  @Autowired
  private RecordService recordService;
  @Autowired
  private Vertx vertx;

  @Override
  public Future<CustomField> save(CustomField customField, OkapiParams params) {
    return repository.maxOrder(params.getTenant())
      .compose(maxOrder -> {
        customField.setOrder(maxOrder + 1);
        return save(customField, params, null);
      });
  }

  private Future<CustomField> save(CustomField customField, OkapiParams params, @Nullable AsyncResult<SQLConnection> connection) {
    final String unAccentName = unAccentName(customField.getName());
    return populateCreator(customField, params)
      .compose(o -> repository.maxRefId(unAccentName, params.getTenant(), connection))
      .compose(maxCount -> {
        customField.setRefId(getCustomFieldId(unAccentName, maxCount));
        return repository.save(customField, params.getTenant(), connection);
      });
  }

  @Override
  public Future<Void> update(String id, CustomField customField, OkapiParams params) {
    return findById(id, params.getTenant())
      .compose(oldCustomField -> {
        customField.setOrder(oldCustomField.getOrder());
          customField.setId(id);
          return update(customField, oldCustomField, params, null);
        });
  }

  private Future<Void> update(CustomField customField, CustomField oldCustomField, OkapiParams params, @Nullable AsyncResult<SQLConnection> connection) {
    final String unAccentName = unAccentName(customField.getName());
    return checkType(customField, oldCustomField)
      .compose(o -> populateUpdater(customField, params))
      .compose(o -> repository.maxRefId(unAccentName, params.getTenant(), connection))
      .compose(maxCount -> {
        customField.setRefId(getCustomFieldId(unAccentName, maxCount));
        return repository.update(customField, params.getTenant(), connection);
      })
      .compose(found -> failIfNotFound(found, customField.getId()));
  }

  @Override
  public Future<CustomField> findById(String id, String tenantId) {
    return repository.findById(id, tenantId)
      .map(customField -> customField.orElseThrow(() -> ServiceExceptions.notFound(CustomField.class, id)));
  }

  @Override
  public Future<CustomFieldCollection> findByQuery(String query, int offset, int limit, String lang, String tenantId) {
    return repository.findByQuery(withSortByOrder(query), offset, limit, tenantId);
  }

  @Override
  public Future<Void> delete(String id, String tenantId) {
    Future<CustomField> cf = findById(id, tenantId);

    return cf
      .compose(field -> recordService.deleteAllValues(field, tenantId))
      .compose(v -> repository.delete(id, tenantId))
      .compose(deleted -> failIfNotFound(deleted, id))
      .compose(v -> updateCustomFieldsOrder(tenantId));
  }

  @Override
  public Future<List<CustomField>> replaceAll(List<CustomField> customFields, OkapiParams params) {
    return repository.findByQuery(null, 0, Integer.MAX_VALUE, params.getTenant())
      .compose(existingFields -> {
        setOrder(customFields);
        customFields.stream()
          .filter(field -> StringUtils.isBlank(field.getId()))
          .forEach(field -> field.setId(UUID.randomUUID().toString()));
        Map<String, CustomField> newFieldsMap = createMapById(customFields);
        Map<String, CustomField> existingFieldsMap = createMapById(existingFields.getCustomFields());

        Set<String> fieldsToRemove = Sets.difference(existingFieldsMap.keySet(), newFieldsMap.keySet());
        Set<String> fieldsToUpdate = Sets.intersection(existingFieldsMap.keySet(), newFieldsMap.keySet());
        Set<String> fieldsToInsert = Sets.difference(newFieldsMap.keySet(), existingFieldsMap.keySet());

        return executeInTransactionWithVertxFuture(params.getTenant(), vertx, (postgresClient, connection) ->
          executeForEach(fieldsToRemove, id -> repository.delete(id, params.getTenant(), connection))
            .compose(deleted ->
              executeForEach(fieldsToUpdate, id -> update(newFieldsMap.get(id), existingFieldsMap.get(id), params, connection)))
            .compose(updateResult ->
              executeForEach(fieldsToInsert, id -> save(newFieldsMap.get(id), params, connection)))
        ).compose(o -> {
          List<CustomField> deletedFields = fieldsToRemove.stream()
            .map(existingFieldsMap::get)
            .collect(Collectors.toList());
          return executeForEach(deletedFields, field -> recordService.deleteAllValues(field, params.getTenant()));
          }
        )
        .map(customFields);
      });
  }

  private void setOrder(List<CustomField> customFields) {
    for (int i = 0; i < customFields.size(); i++) {
      customFields.get(i).setOrder(i + 1);
    }
  }

  private Map<String, CustomField> createMapById(List<CustomField> customFields) {
    return customFields.stream()
      .collect(Collectors.toMap(CustomField::getId, Function.identity()));
  }

  private <T> Future<Void> executeForEach(Collection<T> collection, Function<T, Future<?>> action){
    Future<?> resultFuture = Future.succeededFuture();
    for (T item : collection) {
      resultFuture =  resultFuture.compose(o -> action.apply(item));
    }
    return resultFuture.map(o -> null);
  }


  private Future<Void> updateCustomFieldsOrder(String tenantId) {
    final Future<Void> result = succeededFuture();
    return result
      .compose(v -> repository.findByQuery(null, 0, Integer.MAX_VALUE, tenantId)
      .map(this::updateCustomFieldsOrder)
      .compose(customFields -> updateCustomFields(customFields, tenantId)));
  }

  private Future<Void> updateCustomFields(List<CustomField> customFields, String tenantId) {
    final List<Future> collect = customFields
      .stream()
      .map(customField -> repository.update(customField, tenantId))
      .collect(Collectors.toList());
    return CompositeFuture.all(collect).map((Void) null);
  }

  private List<CustomField> updateCustomFieldsOrder(CustomFieldCollection customFieldsCollection) {
    final List<CustomField> customFields = customFieldsCollection.getCustomFields();
    customFields.sort(Comparator.comparing(CustomField::getOrder));
    for (int i = 0; i < customFields.size(); i++) {
      customFields.get(i).setOrder(i + 1);
    }
    return customFields;
  }

  @Override
  public Future<CustomFieldStatistic> retrieveStatistic(String id, String tenantId) {
    return findById(id, tenantId)
      .compose(field -> recordService.retrieveStatistic(field, tenantId));
  }

  private Future<Void> failIfNotFound(boolean found, String entityId) {
    return found ? succeededFuture() : failedFuture(ServiceExceptions.notFound(CustomField.class, entityId));
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

  private String getCustomFieldId(String id, Integer maxCount) {
    return id + "_" + (maxCount + 1);
  }

  private Future<Object> checkType(CustomField entity, CustomField customField) {
    return !customField.getType().equals(entity.getType())
      ? failedFuture(new IllegalArgumentException("The type of the custom field can not be changed."))
      : succeededFuture();
  }
  /**
   * Adds "sortby order" part to cqlQuery, if query already has "sortby" part, then "order"
   * is added as second sort attribute
   * @param cqlQuery initial query
   * @return query with "sortby order"
   */
  private String withSortByOrder(String cqlQuery) {
    try {
      final CQLParser parser = new CQLParser(CQLParser.V1POINT2);
      CQLNode node = parser.parse(!StringUtils.isBlank(cqlQuery) ? cqlQuery : ALL_RECORDS_QUERY);
      SortVisitor visitor = new SortVisitor();
      node.traverse(visitor);
      CQLSortNode foundSortNode = visitor.getCqlSortNode();
      if(foundSortNode != null){
        foundSortNode.addSortIndex(new ModifierSet(ORDER_ATTRIBUTE));
        return node.toCQL();
      }
      else{
        CQLSortNode newSortNode = new CQLSortNode(node);
        newSortNode.addSortIndex(new ModifierSet(ORDER_ATTRIBUTE));
        return newSortNode.toCQL();
      }
    } catch (CQLParseException | IOException e) {
      throw new IllegalArgumentException("Unsupported Query Format : Search query is in an unsupported format: " + cqlQuery, e);
    }
  }

  private class SortVisitor extends CQLDefaultNodeVisitor{
    private CQLSortNode cqlSortNode;
    @Override
    public void onSortNode(CQLSortNode cqlSortNode) {
      this.cqlSortNode = cqlSortNode;
    }
    public CQLSortNode getCqlSortNode() {
      return cqlSortNode;
    }
  }
}
