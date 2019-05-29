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
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.repositories.EntityRepository;
import presidio.webapp.controllers.entities.EntitiesApi;
import presidio.webapp.model.EntitiesWrapper;
import presidio.webapp.model.Entity;
import presidio.webapp.model.User;
import presidio.webapp.model.UsersWrapper;
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
    private static final String USERS_URI = "/users";
    private static final String USERS_BY_ID_URI = "/users/{userId}";

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
    private presidio.output.domain.records.users.User user1;
    private presidio.output.domain.records.users.User user2;

    private Comparator<Entity> defaultEntityComparator = Comparator.comparing(Entity::getId);

    private Comparator<User> defaultUserComparator = Comparator.comparing(User::getId);


    @Before
    public void setup() {
        //starting up the webapp server
        entitiesApiMVC = MockMvcBuilders.standaloneSetup(entitiesApi).setMessageConverters(mappingJackson2HttpMessageConverter).build();

        this.objectMapper = ObjectMapperProvider.customJsonObjectMapper();

        //save entities in elastic
        entity1 = generateEntity(Collections.singletonList("a"), "entity1", "entityId1", 50d, Collections.singletonList("indicator1"), "user");
        entity2 = generateEntity(Collections.singletonList("b"), "entity2", "entityId2", 60d, Arrays.asList("indicator1", "indicator2"), "ja3");
        List<presidio.output.domain.records.entity.Entity> entityList = Arrays.asList(entity1, entity2);
        entityRepository.saveAll(entityList);
        user1 = generateUser(Collections.singletonList("a"), "entity1", "entityId1", "entity1", 50d, Collections.singletonList("indicator1"));
        user2 = generateUser(Collections.singletonList("b"), "entity2", "entityId2", "entity2", 60d, Arrays.asList("indicator1", "indicator2"));
        user1.setId(entity1.getId());
        user2.setId(entity2.getId());
    }

    @After
    public void cleanTestData() {
        //delete the created entities
        entityRepository.delete(entity1);
        entityRepository.delete(entity2);
    }

    @Test
    public void getAllUsers() throws Exception {

        // init expected response
        User expectedUser1 = convertDomainUserToRestUser(user1);
        User expectedUser2 = convertDomainUserToRestUser(user2);
        UsersWrapper expectedResponse = new UsersWrapper();
        expectedResponse.setTotal(2);
        List<User> users = Arrays.asList(expectedUser1, expectedUser2);
        expectedResponse.setUsers(users);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = entitiesApiMVC.perform(get(USERS_URI))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        UsersWrapper actualResponse = objectMapper.readValue(actualResponseStr, UsersWrapper.class);

        expectedResponse.getUsers().sort(defaultUserComparator);
        actualResponse.getUsers().sort(defaultUserComparator);
        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getUsersFilteredByIndicators() throws Exception {

        // init expected response
        User expectedUser2 = convertDomainUserToRestUser(user2);
        UsersWrapper expectedResponse = new UsersWrapper();
        expectedResponse.setTotal(1);
        List<User> users = Collections.singletonList(expectedUser2);
        expectedResponse.setUsers(users);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = entitiesApiMVC.perform(get(USERS_URI).param("indicatorsName", "indicator2"))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        UsersWrapper actualResponse = objectMapper.readValue(actualResponseStr, UsersWrapper.class);

        expectedResponse.getUsers().sort(defaultUserComparator);
        actualResponse.getUsers().sort(defaultUserComparator);
        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getUsersFilteredByMinScore() throws Exception {

        // init expected response
        User expectedUser2 = convertDomainUserToRestUser(user2); //score 60
        UsersWrapper expectedResponse = new UsersWrapper();
        expectedResponse.setTotal(1);
        List<User> users = Collections.singletonList(expectedUser2);
        expectedResponse.setUsers(users);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = entitiesApiMVC.perform(get(USERS_URI).param("minScore", "55"))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        UsersWrapper actualResponse = objectMapper.readValue(actualResponseStr, UsersWrapper.class);

        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getUserById() throws Exception {

        // init expected response
        User expectedUser1 = convertDomainUserToRestUser(user1);

        // get actual response
        MvcResult mvcResult = entitiesApiMVC.perform(get(USERS_BY_ID_URI, user1.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        User actualResponse = objectMapper.readValue(actualResponseStr, User.class);

        Assert.assertEquals(expectedUser1, actualResponse);
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

    private presidio.output.domain.records.users.User generateUser(List<String> classifications, String userName, String userId, String displayName, double score, List<String> indicators) {
        return new presidio.output.domain.records.users.User(userId, userName, displayName, score, classifications, indicators, new ArrayList<>(), UserSeverity.CRITICAL, 0);
    }

    private User convertDomainUserToRestUser(presidio.output.domain.records.users.User user) {
        User convertedUser = new User();
        if (ObjectUtils.isEmpty(user))
            return null;
        convertedUser.setId(user.getId());
        convertedUser.setUserDisplayName(user.getUserDisplayName());
        if (user.getSeverity() != null) {
            convertedUser.setSeverity(convertUserSeverity(user.getSeverity()));
        }
        convertedUser.setScore((int) user.getScore());
        convertedUser.setTags(user.getTags());
        convertedUser.setUsername(user.getUserName());
        convertedUser.setAlertClassifications(user.getAlertClassifications());
        convertedUser.setAlertsCount(user.getAlertsCount());
        convertedUser.setUserId(user.getUserId());
        return convertedUser;
    }

    private presidio.webapp.model.UserQueryEnums.UserSeverity convertUserSeverity(UserSeverity userSeverity) {
        return presidio.webapp.model.UserQueryEnums.UserSeverity.valueOf(userSeverity.name());
    }
}

