package org.folio.service;

import static org.jeasy.random.FieldPredicates.named;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import io.vertx.core.Future;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.misc.EnumRandomizer;
import org.jeasy.random.randomizers.misc.UUIDRandomizer;
import org.jeasy.random.randomizers.text.StringDelegatingRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import org.folio.rest.jaxrs.model.CustomField;
import org.folio.rest.jaxrs.model.CustomFieldStatisticCollection;
import org.folio.test.junit.TestStartLoggingRule;

public class NoOpRecordServiceTest {

  @Rule
  public TestRule watcher = TestStartLoggingRule.instance();

  private static EasyRandom cfRandom;
  private static StringRandomizer tenantIdRandom;

  private CustomField field;
  private String tenantId;
  private NoOpRecordService service;


  @BeforeClass
  public static void setUpClass() {
    EasyRandomParameters params = new EasyRandomParameters()
      .randomize(named("id"), StringDelegatingRandomizer.aNewStringDelegatingRandomizer(
        UUIDRandomizer.aNewUUIDRandomizer()))
      .randomize(named("name"), StringRandomizer.aNewStringRandomizer(20))
      .randomize(named("refId"), StringRandomizer.aNewStringRandomizer(20))
      .randomize(named("entityType"), StringDelegatingRandomizer.aNewStringDelegatingRandomizer(
        EnumRandomizer.aNewEnumRandomizer(EntityType.class)))
      .excludeField(named("metadata"));

    cfRandom = new EasyRandom(params);

    tenantIdRandom = StringRandomizer.aNewStringRandomizer(10);
  }

  @Before
  public void setUp() {
    field = nextRandomCustomField();
    tenantId = nextRandomTenantId();

    service = new NoOpRecordService();
  }

  @Test
  public void shouldReturnEmptyStatistic() {
    Future<CustomFieldStatisticCollection> stat = service.retrieveStatistic(field, tenantId);

    assertNotNull(stat);
    assertTrue(stat.succeeded());
    assertEquals(stat.result(), new CustomFieldStatisticCollection()
      .withStats(Collections.emptyList())
      .withTotalRecords(0));
  }

  @Test
  public void shouldReturnSuccessOnDeleteAllValues() {
    Future<Void> res = service.deleteAllValues(field, tenantId);

    assertNotNull(res);
    assertTrue(res.succeeded());
  }

  private static CustomField nextRandomCustomField() {
    return cfRandom.nextObject(CustomField.class);
  }

  private static String nextRandomTenantId() {
    return tenantIdRandom.getRandomValue();
  }

  private enum EntityType {

    PROVIDER("provider"),
    PACKAGE("package"),
    TITLE("title"),
    RESOURCE("resource"),
    USER("user"),
    ORDER("order"),
    ORDERLINE("orderline"),
    ITEM("item"),
    REQUEST("request");

    private final String value;

    EntityType(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }

  }
}
