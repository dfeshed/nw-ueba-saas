package presidio.webapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.rest.jsonpatch.JsonPatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.webapp.model.EntitiesWrapper;
import presidio.webapp.model.EntityQuery;
import presidio.webapp.spring.RestServiceTestConfig;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.notNull;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RestServiceTestConfig.class)
public class RestEntityServiceTest {

    @Autowired
    AlertPersistencyService alertPersistencyService;

    @Autowired
    EntityPersistencyService entityPersistencyService;

    @Autowired
    RestEntityService restEntityService;

    @Test
    public void testReturnEntityWithoutExpand() {
        Entity entity = createEntity(1);
        when(entityPersistencyService.findEntityByDocumentId(eq(entity.getId()))).thenReturn(entity);
        presidio.webapp.model.Entity resultEntity = restEntityService.getEntityByDocumentId(entity.getId(), false);
        assertNotNull(resultEntity);
        assertEquals(entity.getEntityId(), resultEntity.getEntityId());
        assertEquals(entity.getAlertClassifications(), resultEntity.getAlertClassifications());
        assertEquals(entity.getAlertsCount(), resultEntity.getAlertsCount().intValue());
        assertEquals(entity.getId(), resultEntity.getId());
        assertEquals(0, Double.compare(entity.getScore(), resultEntity.getScore().doubleValue()));
        assertEquals(entity.getSeverity().toString(), resultEntity.getSeverity().toString());
        assertEquals(entity.getEntityName(), resultEntity.getEntityName());
        assertEquals(entity.getEntityType(), resultEntity.getEntityType());
    }

    @Test
    public void testReturnEntityWithExpand() {
        Alert alert = createAlert(1);
        Page<Alert> page = new PageImpl<Alert>(new ArrayList<>(Arrays.asList(alert)));
        when(alertPersistencyService.findByEntityDocumentId(eq(alert.getEntityDocumentId()), notNull(PageRequest.class))).thenReturn(page);
        Entity entity = createEntity(1);
        when(entityPersistencyService.findEntityByDocumentId(eq(entity.getEntityId()))).thenReturn(entity);
        presidio.webapp.model.Entity resultEntity = restEntityService.getEntityByDocumentId("entityId1", true);
        assertEquals(1, resultEntity.getAlerts().size());
    }

    @Test
    public void testReturnEntitiesWithoutExpand() {
        Entity entity3 = createEntity(3);
        entity3.setScore(90);
        Entity entity4 = createEntity(4);
        entity4.setScore(90);
        Entity entity5 = createEntity(5);
        entity5.setScore(90);
        Page<Entity> page = new PageImpl<Entity>(new ArrayList<>(Arrays.asList(entity3, entity4, entity5)), null, 5);
        when(entityPersistencyService.find(notNull(presidio.output.domain.records.entity.EntityQuery.class))).thenReturn(page);
        EntityQuery entityQuery = new EntityQuery();
        entityQuery.setExpand(false);
        entityQuery.setMinScore(70);
        entityQuery.setMaxScore(100);
        EntitiesWrapper entitiesWrapper = restEntityService.getEntities(entityQuery);
        List<presidio.webapp.model.Entity> resultEntity = entitiesWrapper.getEntities();

        assertEquals(3, resultEntity.size());
        assertEquals(5, entitiesWrapper.getTotal().intValue());
    }

    @Test
    public void testReturnEntitiesWithIndicatorsFilter() {
        Entity entity1 = createEntity(1);
        entity1.setIndicators(Arrays.asList("indicator1"));
        Entity entity2 = createEntity(2);
        entity2.setIndicators(Arrays.asList("indicator1", "indicator2"));

        Page<Entity> page = new PageImpl<Entity>(new ArrayList<>(Arrays.asList(entity2)), null, 2);
        presidio.output.domain.records.entity.EntityQuery domainQuery = new presidio.output.domain.records.entity.EntityQuery.EntityQueryBuilder()
                .filterByIndicators(Arrays.asList("indicator2")).build();
        when(entityPersistencyService.find(domainQuery)).thenReturn(page);
        EntityQuery entityQuery = new EntityQuery();
        entityQuery.setExpand(false);
        entityQuery.setIndicatorsName(Arrays.asList("indicator2"));
        EntitiesWrapper entitiesWrapper = restEntityService.getEntities(entityQuery);
        List<presidio.webapp.model.Entity> resultEntity = entitiesWrapper.getEntities();

        assertEquals(1, resultEntity.size());
        assertEquals(2, entitiesWrapper.getTotal().intValue());
    }


    @Test
    public void testReturnEntitiesWithExpand() {
        Entity entity1 = createEntity(1);
        Entity entity2 = createEntity(2);
        Entity entity3 = createEntity(3);
        Alert alert1 = createAlert(1);
        Alert alert2 = createAlert(2);
        Alert alert3 = createAlert(3);
        Alert alert4 = createAlert(4);
        alert4.setEntityDocumentId(entity2.getId());
        alert4.setEntityName(entity2.getEntityName());
        entity2.setAlertsCount(2);

        Page<Entity> entityPage = new PageImpl<>(new ArrayList<>(Arrays.asList(entity1, entity2, entity3)));
        when(entityPersistencyService.find(notNull(presidio.output.domain.records.entity.EntityQuery.class))).thenReturn(entityPage);
        EntityQuery entityQuery = new EntityQuery();
        entityQuery.setExpand(true);
        Page<Alert> firstPage = new PageImpl<>(new ArrayList<>(Arrays.asList(alert1)));
        Page<Alert> secondPage = new PageImpl<>(new ArrayList<>(Arrays.asList(alert4, alert2)));
        Page<Alert> thirdPage = new PageImpl<>(new ArrayList<>(Arrays.asList(alert3)));
        when(alertPersistencyService.findByEntityDocumentId(notNull(String.class), notNull(PageRequest.class))).thenReturn(firstPage, secondPage, thirdPage);
        List<presidio.webapp.model.Entity> resultEntity = restEntityService.getEntities(entityQuery).getEntities();
        resultEntity.forEach(entity -> {
            if (entity.getId().equals(entity1.getId()) || entity.getId().equals(entity3.getId()))
                Assert.assertEquals(1, entity.getAlerts().size());
            if (entity.getId().equals("entityentity1"))
                assertEquals(1, entity.getAlerts().size());
            else {
                if (entity.getId().equals(entity2.getId())) {
                    Assert.assertEquals(2, entity.getAlerts().size());
                }
            }
        });
    }

    @Test
    public void testUpdateEntity_addFirstTag() throws IOException {
        Entity entity = createEntity(1);
        String patchOperationString = "{\"operations\":[{ \"op\": \"add\", \"path\": \"/tags/-\", \"value\":\"1\"}]}";
        JsonNode jsonNode = ObjectMapperProvider.defaultJsonObjectMapper().readTree(patchOperationString);
        when(entityPersistencyService.findEntityByDocumentId(anyString())).thenReturn(entity);
        when(entityPersistencyService.save(Matchers.any(Entity.class))).thenReturn(entity);

        presidio.webapp.model.Entity updatedEntity = restEntityService.updateEntity(entity.getId(), JsonPatch.fromJson(jsonNode));
        assertNotNull(updatedEntity.getTags());
        assertEquals(1, updatedEntity.getTags().size());
    }

    @Test
    public void testUpdateEntity_addTag() throws IOException {
        Entity entity = createEntity(1);
        entity.setTags(Arrays.asList("Tag"));

        String patchOperationString = "{\"operations\":[{ \"op\": \"add\", \"path\": \"/tags/-\", \"value\":\"1\"}]}";
        JsonNode jsonNode = ObjectMapperProvider.defaultJsonObjectMapper().readTree(patchOperationString);

        when(entityPersistencyService.findEntityByDocumentId(anyString())).thenReturn(entity);
        when(entityPersistencyService.save(Matchers.any(Entity.class))).thenReturn(entity);

        presidio.webapp.model.Entity updatedEntity = restEntityService.updateEntity(entity.getId(), JsonPatch.fromJson(jsonNode));
        assertNotNull(updatedEntity.getTags());
        assertEquals(2, updatedEntity.getTags().size());
    }

    @Test
    public void testUpdateEntity_addExistingTag() throws IOException {
        Entity entity = createEntity(1);
        entity.setTags(Arrays.asList("Tag"));

        String patchOperationString = "{\"operations\":[{ \"op\": \"add\", \"path\": \"/tags/-\", \"value\":\"Tag\"}]}";
        JsonNode jsonNode = ObjectMapperProvider.defaultJsonObjectMapper().readTree(patchOperationString);

        when(entityPersistencyService.findEntityByDocumentId(anyString())).thenReturn(entity);
        when(entityPersistencyService.save(Matchers.any(Entity.class))).thenReturn(entity);

        presidio.webapp.model.Entity updatedEntity = restEntityService.updateEntity(entity.getId(), JsonPatch.fromJson(jsonNode));
        assertNotNull(updatedEntity.getTags());
        assertEquals(1, updatedEntity.getTags().size());
    }

    @Test
    public void testUpdateEntity_removeTag() throws IOException {
        Entity entity = createEntity(1);
        entity.setTags(Arrays.asList("Tag", "anotherTag"));

        String patchOperationString = "{\"operations\":[{ \"op\": \"remove\", \"path\": \"/tags/-\", \"value\":\"Tag\"}]}";
        JsonNode jsonNode = ObjectMapperProvider.defaultJsonObjectMapper().readTree(patchOperationString);

        when(entityPersistencyService.findEntityByDocumentId(anyString())).thenReturn(entity);
        when(entityPersistencyService.save(Matchers.any(Entity.class))).thenReturn(entity);

        presidio.webapp.model.Entity updatedEntity = restEntityService.updateEntity(entity.getId(), JsonPatch.fromJson(jsonNode));
        assertNotNull(updatedEntity.getTags());
        assertEquals(1, updatedEntity.getTags().size());
    }

    @Test
    public void testUpdateEntity_removeNotExistingTag() throws IOException {
        Entity entity = createEntity(1);
        entity.setTags(Arrays.asList("Tag", "anotherTag"));

        String patchOperationString = "{\"operations\":[{ \"op\": \"remove\", \"path\": \"/tags/-\", \"value\":\"notExistingTag\"}]}";
        JsonNode jsonNode = ObjectMapperProvider.defaultJsonObjectMapper().readTree(patchOperationString);

        when(entityPersistencyService.findEntityByDocumentId(anyString())).thenReturn(entity);
        when(entityPersistencyService.save(Matchers.any(Entity.class))).thenReturn(entity);

        presidio.webapp.model.Entity updatedEntity = restEntityService.updateEntity(entity.getId(), JsonPatch.fromJson(jsonNode));
        assertNotNull(updatedEntity.getTags());
        assertEquals(2, updatedEntity.getTags().size());
    }

    private Entity createEntity(int number) {
        Entity entity = new Entity();
        entity.setEntityName("entity" + number);
        entity.setId("id" + number);
        entity.setEntityId("entityId" + number);
        entity.setAlertsCount(1);
        entity.setScore(60);
        entity.setSeverity(EntitySeverity.MEDIUM);
        List classifications = new ArrayList(Arrays.asList("Mass Changes to Critical Enterprise Groups"));
        entity.setAlertClassifications(classifications);
        entity.setEntityType("ja3");
        return entity;
    }

    private Alert createAlert(int number) {
        List<String> classifications = new ArrayList<>(Arrays.asList("Mass Changes to Critical Enterprise Groups"));
        return new Alert("entityId" + number, "smartId", classifications, "entity" + number, "entity" + number,
                Date.from(Instant.parse("2017-01-01T00:00:00Z")), Date.from(Instant.parse("2017-01-01T11:00:00Z")),
                10, 10, AlertEnums.AlertTimeframe.DAILY, AlertEnums.AlertSeverity.CRITICAL, null, 0D, "entityType");
    }
}
