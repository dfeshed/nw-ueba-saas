package presidio.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.test.category.ModuleTestCategory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ObjectUtils;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.repositories.EntityRepository;
import presidio.webapp.controllers.entities.EntitiesApi;
import presidio.webapp.model.EntitiesWrapper;
import presidio.webapp.model.Entity;
import presidio.webapp.spring.ApiControllerModuleTestConfig;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiControllerModuleTestConfig.class)
@Category(ModuleTestCategory.class)
public class EntityApiControllerModuleTest {

    private static final String ENTITIES_URI = "/entities";
    private static final String ENTITIES_BY_ID_URI = "/entities/{entityId}";

    private MockMvc entitiesApiMVC;

    @Autowired
    private EntitiesApi entitiesApi;

    @Autowired
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private EntityRepository entityRepository;

    private ObjectMapper objectMapper;

    private presidio.output.domain.records.entity.Entity entity1;
    private presidio.output.domain.records.entity.Entity entity2;
    private Comparator<Entity> defaultEntityComparator = Comparator.comparing(Entity::getId);


    @Before
    public void setup() {
        //starting up the webapp server
        entitiesApiMVC = MockMvcBuilders.standaloneSetup(entitiesApi).setMessageConverters(mappingJackson2HttpMessageConverter).build();

        this.objectMapper = ObjectMapperProvider.customJsonObjectMapper();

        //save entities in elastic
        entity1 = generateEntity(Collections.singletonList("a"), "entity1", "entityId1", 50d, Collections.singletonList("indicator1"), "userId");
        entity2 = generateEntity(Collections.singletonList("b"), "entity2", "entityId2", 60d, Arrays.asList("indicator1", "indicator2"), "ja3");
        List<presidio.output.domain.records.entity.Entity> entityList = Arrays.asList(entity1, entity2);
        entityRepository.save(entityList);
    }

    @After
    public void cleanTestData() {
        //delete the created entities
        entityRepository.delete(entity1);
        entityRepository.delete(entity2);
    }

    @Test
    public void getAllEntities() throws Exception {

        // init expected response
        Entity expectedEntity1 = convertDomainEntityToRestEntity(entity1);
        Entity expectedEntity2 = convertDomainEntityToRestEntity(entity2);
        EntitiesWrapper expectedResponse = new EntitiesWrapper();
        expectedResponse.setTotal(2);
        List<Entity> entities = Arrays.asList(expectedEntity1, expectedEntity2);
        expectedResponse.setEntities(entities);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = entitiesApiMVC.perform(get(ENTITIES_URI))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        EntitiesWrapper actualResponse = objectMapper.readValue(actualResponseStr, EntitiesWrapper.class);

        expectedResponse.getEntities().sort(defaultEntityComparator);
        actualResponse.getEntities().sort(defaultEntityComparator);
        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getEntitiesFilteredByIndicators() throws Exception {

        // init expected response
        Entity expectedEntity2 = convertDomainEntityToRestEntity(entity2);
        EntitiesWrapper expectedResponse = new EntitiesWrapper();
        expectedResponse.setTotal(1);
        List<Entity> entities = Collections.singletonList(expectedEntity2);
        expectedResponse.setEntities(entities);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = entitiesApiMVC.perform(get(ENTITIES_URI).param("indicatorsName", "indicator2"))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        EntitiesWrapper actualResponse = objectMapper.readValue(actualResponseStr, EntitiesWrapper.class);

        expectedResponse.getEntities().sort(defaultEntityComparator);
        actualResponse.getEntities().sort(defaultEntityComparator);
        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getEntitiesFilteredByMinScore() throws Exception {

        // init expected response
        Entity expectedEntity2 = convertDomainEntityToRestEntity(entity2); //score 60
        EntitiesWrapper expectedResponse = new EntitiesWrapper();
        expectedResponse.setTotal(1);
        List<Entity> entities = Collections.singletonList(expectedEntity2);
        expectedResponse.setEntities(entities);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = entitiesApiMVC.perform(get(ENTITIES_URI).param("minScore", "55"))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        EntitiesWrapper actualResponse = objectMapper.readValue(actualResponseStr, EntitiesWrapper.class);

        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getEntitiesFilteredByType() throws Exception {

        // init expected response
        Entity expectedEntity2 = convertDomainEntityToRestEntity(entity2);
        EntitiesWrapper expectedResponse = new EntitiesWrapper();
        expectedResponse.setTotal(1);
        List<Entity> entities = Collections.singletonList(expectedEntity2);
        expectedResponse.setEntities(entities);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = entitiesApiMVC.perform(get(ENTITIES_URI).param("entityType", "ja3"))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        EntitiesWrapper actualResponse = objectMapper.readValue(actualResponseStr, EntitiesWrapper.class);

        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getEntityById() throws Exception {

        // init expected response
        Entity expectedEntity1 = convertDomainEntityToRestEntity(entity1);

        // get actual response
        MvcResult mvcResult = entitiesApiMVC.perform(get(ENTITIES_BY_ID_URI, entity1.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        Entity actualResponse = objectMapper.readValue(actualResponseStr, Entity.class);

        Assert.assertEquals(expectedEntity1, actualResponse);
    }


    private presidio.output.domain.records.entity.Entity generateEntity(List<String> classifications, String entityName, String entityId, double score, List<String> indicators, String entityType) {
        return new presidio.output.domain.records.entity.Entity(entityId, entityName, score, classifications, indicators, new ArrayList<>(), EntitySeverity.CRITICAL, 0, entityType);
    }

    private Entity convertDomainEntityToRestEntity(presidio.output.domain.records.entity.Entity entity) {
        Entity convertedEntity = new Entity();
        if (ObjectUtils.isEmpty(entity))
            return null;
        convertedEntity.setId(entity.getId());
        if (entity.getSeverity() != null) {
            convertedEntity.setSeverity(convertEntitySeverity(entity.getSeverity()));
        }
        convertedEntity.setScore((int) entity.getScore());
        convertedEntity.setTags(entity.getTags());
        convertedEntity.setEntityName(entity.getEntityName());
        convertedEntity.setAlertClassifications(entity.getAlertClassifications());
        convertedEntity.setAlertsCount(entity.getAlertsCount());
        convertedEntity.setEntityId(entity.getEntityId());
        convertedEntity.setEntityType(entity.getEntityType());
        return convertedEntity;
    }

    private presidio.webapp.model.EntityQueryEnums.EntitySeverity convertEntitySeverity(presidio.output.domain.records.entity.EntitySeverity entitySeverity) {
        return presidio.webapp.model.EntityQueryEnums.EntitySeverity.valueOf(entitySeverity.name());
    }
}

