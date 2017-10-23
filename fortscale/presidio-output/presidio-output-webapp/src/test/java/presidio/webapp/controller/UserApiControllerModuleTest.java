package presidio.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import fortscale.utils.elasticsearch.config.ElasticsearchTestUtils;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.test.category.ModuleTestCategory;
import org.junit.*;
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
import org.springframework.web.client.RestTemplate;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.repositories.UserRepository;
import presidio.webapp.controllers.users.UsersApi;
import presidio.webapp.model.User;
import presidio.webapp.model.UsersWrapper;
import presidio.webapp.spring.ApiControllerModuleTestConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApiControllerModuleTestConfig.class)
@Category(ModuleTestCategory.class)
public class UserApiControllerModuleTest {

    private static final String USERS_URI = "/users";
    private static final String USERS_BY_ID_URI = "/users/{userId}";

    private MockMvc usersApiMVC;

    @Autowired
    private UsersApi usersApi;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PresidioElasticsearchTemplate esTemplate;

    private ObjectMapper objectMapper;
    private static ElasticsearchTestUtils embeddedElasticsearchUtils = new ElasticsearchTestUtils();

    private presidio.output.domain.records.users.User user1;
    private presidio.output.domain.records.users.User user2;

    private Comparator<User> defaultUserComparator = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

    @BeforeClass
    public static void setupElasticsearch() {
        try {
            embeddedElasticsearchUtils.setupLocalElasticsearch();
        } catch (Exception e) {
            Assert.fail("Failed to start elasticsearch");
            embeddedElasticsearchUtils.stopEmbeddedElasticsearch();
        }
    }

    @Before
    public void setup() {
        //starting up the webapp server
        usersApiMVC = MockMvcBuilders.standaloneSetup(usersApi).setMessageConverters(mappingJackson2HttpMessageConverter).build();

        this.objectMapper = ObjectMapperProvider.customJsonObjectMapper();

        esTemplate.deleteIndex(presidio.output.domain.records.users.User.class);
        esTemplate.createIndex(presidio.output.domain.records.users.User.class);
        esTemplate.putMapping(presidio.output.domain.records.users.User.class);
        esTemplate.refresh(presidio.output.domain.records.users.User.class);

        //save users in elastic
        user1 = generateUser(Arrays.asList("a"), "user1", "userId1", "user1", 50d, Arrays.asList("indicator1"));
        user2 = generateUser(Arrays.asList("b"), "user2", "userId2", "user2", 60d, Arrays.asList("indicator1", "indicator2"));
        List<presidio.output.domain.records.users.User> userList = Arrays.asList(user1, user2);
        userRepository.save(userList);
    }

    @After
    public void tearDown() {
        //delete the created users
        userRepository.delete(user1);
        userRepository.delete(user2);
    }

    @AfterClass
    public static void stopElasticsearch() throws Exception {
        embeddedElasticsearchUtils.stopEmbeddedElasticsearch();
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
        MvcResult mvcResult = usersApiMVC.perform(get(USERS_URI))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        UsersWrapper actualResponse = objectMapper.readValue(actualResponseStr, UsersWrapper.class);

        Collections.sort(expectedResponse.getUsers(), defaultUserComparator);
        Collections.sort(actualResponse.getUsers(), defaultUserComparator);
        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getUsersFilteredByIndicators() throws Exception {

        // init expected response
        User expectedUser2 = convertDomainUserToRestUser(user2);
        UsersWrapper expectedResponse = new UsersWrapper();
        expectedResponse.setTotal(1);
        List<User> users = Arrays.asList(expectedUser2);
        expectedResponse.setUsers(users);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = usersApiMVC.perform(get(USERS_URI).param("indicatorsName", "indicator2"))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        UsersWrapper actualResponse = objectMapper.readValue(actualResponseStr, UsersWrapper.class);

        Collections.sort(expectedResponse.getUsers(), defaultUserComparator);
        Collections.sort(actualResponse.getUsers(), defaultUserComparator);
        Assert.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getUsersFilteredByMinScore() throws Exception {

        // init expected response
        User expectedUser2 = convertDomainUserToRestUser(user2); //score 60
        UsersWrapper expectedResponse = new UsersWrapper();
        expectedResponse.setTotal(1);
        List<User> users = Arrays.asList(expectedUser2);
        expectedResponse.setUsers(users);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = usersApiMVC.perform(get(USERS_URI).param("minScore", "55"))
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
        MvcResult mvcResult = usersApiMVC.perform(get(USERS_BY_ID_URI, user1.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        User actualResponse = objectMapper.readValue(actualResponseStr, User.class);

        Assert.assertEquals(expectedUser1, actualResponse);
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
        return convertedUser;
    }

    private presidio.webapp.model.UserQueryEnums.UserSeverity convertUserSeverity(UserSeverity userSeverity) {
        return presidio.webapp.model.UserQueryEnums.UserSeverity.valueOf(userSeverity.name());
    }
}
