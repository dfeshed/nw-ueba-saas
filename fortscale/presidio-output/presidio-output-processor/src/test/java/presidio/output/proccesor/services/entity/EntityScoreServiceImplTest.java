package presidio.output.proccesor.services.entity;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.commons.services.spring.AlertSeverityServiceConfig;
import presidio.output.commons.services.entity.EntitySeverityService;
import presidio.output.commons.services.entity.EntitySeverityServiceImpl;
import presidio.output.domain.records.PresidioRange;
import presidio.output.domain.records.EntitySeveritiesRangeDocument;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntityQuery;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.repositories.EntitySeveritiesRangeRepository;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig;
import presidio.output.proccesor.spring.TestConfig;
import presidio.output.processor.spring.EntityServiceConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shays on 27/08/2017.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        TestConfig.class,
        EntityServiceConfig.class,
        EventPersistencyServiceConfig.class,
        PresidioOutputPersistencyServiceConfig.class,
        MongodbTestConfig.class,
        AlertSeverityServiceConfig.class,
        ElasticsearchTestConfig.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class EntityScoreServiceImplTest {
    @MockBean
    private EntityPersistencyService mockEntityPersistence;
    @MockBean
    private EntitySeveritiesRangeRepository entitySeveritiesRangeRepository;

    @Autowired
    private EntitySeverityService entitySeverityService;

    @Test
    public void testGetSeveritiesMap_RecalculateSeverities() {
        //Create page1 with 10 entities and page2 with 5 entities
        List<Entity> page1Entities = new ArrayList<>();
        List<Entity> page2Entities = new ArrayList<>();
        List<Entity> page3Entities = new ArrayList<>();

        //noinspection Duplicates
        for (int i = 0; i < 25; i++) {
            Entity e = new Entity();
            e.setScore(i * 10D);

            if (i < 10) {
                page1Entities.add(e);
            } else if (i < 20) {
                page2Entities.add(e);
            } else {
                page3Entities.add(e);
            }
        }

        Pageable pageable1 = new PageRequest(0, 10);
        Page<Entity> page1 = new PageImpl<>(page1Entities, pageable1, 25);
        Pageable pageable2 = new PageRequest(1, 10);
        Page<Entity> page2 = new PageImpl<>(page2Entities, pageable2, 25);
        Pageable pageable3 = new PageRequest(2, 10);
        Page<Entity> page3 = new PageImpl<>(page3Entities, pageable3, 25);
        Mockito.when(mockEntityPersistence.find(Mockito.any(EntityQuery.class))).thenAnswer(invocation -> {
            EntityQuery query = (EntityQuery)invocation.getArguments()[0];

            //noinspection Duplicates
            if (query.getPageNumber() == 0) {
                return page1;
            } else if (query.getPageNumber() == 1) {
                return page2;
            } else {
                return page3;
            }
        });

        Mockito.verify(Mockito.spy(EntitySeveritiesRangeRepository.class), Mockito.times(0)).findOne(EntitySeveritiesRangeDocument.getEntitySeveritiesDocIdName("entityType"));
        EntitySeverityServiceImpl.EntityScoreToSeverity severityTreeMap = entitySeverityService.getSeveritiesMap(true, "entityType");
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(55D));
        Assert.assertEquals(EntitySeverity.MEDIUM, severityTreeMap.getEntitySeverity(270D));
        Assert.assertEquals(EntitySeverity.HIGH, severityTreeMap.getEntitySeverity(350D));
        Assert.assertEquals(EntitySeverity.CRITICAL, severityTreeMap.getEntitySeverity(520D));
        //Special cases
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(-5D));
        Assert.assertEquals(EntitySeverity.CRITICAL, severityTreeMap.getEntitySeverity(700D));
    }

    @Test
    public void testGetSeveritiesMap_NoRecalculateSeverities_DefaultSeverities() {
        Iterable<EntitySeveritiesRangeDocument> percentileScores = new ArrayList<>();
        Mockito.when(entitySeveritiesRangeRepository.findAll()).thenReturn(percentileScores);
        EntitySeverityServiceImpl.EntityScoreToSeverity severityTreeMap = entitySeverityService.getSeveritiesMap(false, "entityType");
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(55D));
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(2D));
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(56D));
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(120D));
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(185D));
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(122D));
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(186D));
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(240D));
        //Special cases
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(-5D));
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(300D));
    }

    @Test
    public void testGetSeveritiesMap_NoRecalculateSeverities_ExistingSeverities() {
        String entityType = "userId";
        EntitySeveritiesRangeDocument entitySeveritiesRangeDocument = new EntitySeveritiesRangeDocument(entityType);
        Map<EntitySeverity, PresidioRange<Double>> map = new LinkedHashMap<>();
        map.put(EntitySeverity.LOW, new PresidioRange<>(0d, 240d));
        map.put(EntitySeverity.MEDIUM, new PresidioRange<>(264d, 264d));
        map.put(EntitySeverity.HIGH, new PresidioRange<>(343.2d, 343.2d));
        map.put(EntitySeverity.CRITICAL, new PresidioRange<>(518.8d, 514.8d));
        entitySeveritiesRangeDocument.setSeverityToScoreRangeMap(map);
        Mockito.when(entitySeveritiesRangeRepository.findOne(EntitySeveritiesRangeDocument.getEntitySeveritiesDocIdName(entityType))).thenReturn(entitySeveritiesRangeDocument);
        EntitySeverityServiceImpl.EntityScoreToSeverity severityTreeMap = entitySeverityService.getSeveritiesMap(false, entityType);
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(50D));
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(250D));
        Assert.assertEquals(EntitySeverity.MEDIUM, severityTreeMap.getEntitySeverity(270D));
        Assert.assertEquals(EntitySeverity.MEDIUM, severityTreeMap.getEntitySeverity(340D));
        Assert.assertEquals(EntitySeverity.HIGH, severityTreeMap.getEntitySeverity(350));
        Assert.assertEquals(EntitySeverity.HIGH, severityTreeMap.getEntitySeverity(500D));
        Assert.assertEquals(EntitySeverity.CRITICAL, severityTreeMap.getEntitySeverity(520D));
        Assert.assertEquals(EntitySeverity.CRITICAL, severityTreeMap.getEntitySeverity(600D));
        //Special cases
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(-5D));
        Assert.assertEquals(EntitySeverity.LOW, severityTreeMap.getEntitySeverity(0D));
    }

    @Test
    public void getScoresArray() throws Exception {
        //Create page1 with 10 entities and page2 with 5 entities
        List<Entity> page1Entities = new ArrayList<>();
        List<Entity> page2Entities = new ArrayList<>();
        List<Entity> page3Entities = new ArrayList<>();

        //noinspection Duplicates
        for (int i = 0; i < 25; i++) {
            Entity e = new Entity();
            e.setScore(i * 10D);

            if (i < 10) {
                page1Entities.add(e);
            } else if (i < 20) {
                page2Entities.add(e);
            } else {
                page3Entities.add(e);
            }
        }

        Pageable pageable1 = new PageRequest(0, 10);
        Page<Entity> page1 = new PageImpl<>(page1Entities, pageable1, 25);
        Pageable pageable2 = new PageRequest(1, 10);
        Page<Entity> page2 = new PageImpl<>(page2Entities, pageable2, 25);
        Pageable pageable3 = new PageRequest(2, 10);
        Page<Entity> page3 = new PageImpl<>(page3Entities, pageable3, 25);
        Mockito.when(mockEntityPersistence.find(Mockito.any(EntityQuery.class))).thenAnswer((Answer<Page>) invocation -> {
            EntityQuery query = (EntityQuery)invocation.getArguments()[0];

            //noinspection Duplicates
            if (query.getPageNumber() == 0) {
                return page1;
            } else if (query.getPageNumber() == 1) {
                return page2;
            } else {
                return page3;
            }
        });

        double[] scores = Whitebox.invokeMethod(entitySeverityService, "getScoresArray", "entityType");
        Assert.assertEquals(25, scores.length, 0.1);
        Assert.assertEquals(0D, scores[0], 0.1);
        Assert.assertEquals(240D, scores[24], 0.1);
    }

    @Test
    public void testUpdateSeveritiesForEntitiesPage() throws Exception {
        List<Entity> page = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Entity e = new Entity();
            e.setScore(i * 10D);
            page.add(e);
        }

        Pageable pageable1 = new PageRequest(0, 10);
        Page<Entity> page1 = new PageImpl<>(page, pageable1, 10);
        Map<EntitySeverity, PresidioRange<Double>> map = new LinkedHashMap<>();
        map.put(EntitySeverity.LOW, new PresidioRange<>(0d, 300d));
        map.put(EntitySeverity.MEDIUM, new PresidioRange<>(30d, 50d));
        map.put(EntitySeverity.HIGH, new PresidioRange<>(50d, 90d));
        map.put(EntitySeverity.CRITICAL, new PresidioRange<>(90d, 90d));
        EntitySeverityServiceImpl.EntityScoreToSeverity entityScoreToSeverity = new EntitySeverityServiceImpl.EntityScoreToSeverity(map);
        Whitebox.invokeMethod(entitySeverityService, "updateEntitySeverities", entityScoreToSeverity, page1.getContent());
        Assert.assertEquals(EntitySeverity.LOW, page.get(0).getSeverity());
        Assert.assertEquals(EntitySeverity.LOW, page.get(1).getSeverity());
        Assert.assertEquals(EntitySeverity.LOW, page.get(2).getSeverity());
        Assert.assertEquals(EntitySeverity.MEDIUM, page.get(3).getSeverity());
        Assert.assertEquals(EntitySeverity.MEDIUM, page.get(4).getSeverity());
        Assert.assertEquals(EntitySeverity.HIGH, page.get(5).getSeverity());
        Assert.assertEquals(EntitySeverity.HIGH, page.get(6).getSeverity());
        Assert.assertEquals(EntitySeverity.HIGH, page.get(7).getSeverity());
        Assert.assertEquals(EntitySeverity.HIGH, page.get(8).getSeverity());
        Assert.assertEquals(EntitySeverity.CRITICAL, page.get(9).getSeverity());
    }
}
