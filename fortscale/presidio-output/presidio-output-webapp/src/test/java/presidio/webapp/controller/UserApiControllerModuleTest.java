package presidio.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.RestTemplateConfig;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.category.ModuleTestCategory;
import org.apache.commons.collections.CollectionUtils;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.repositories.UserRepository;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.webapp.controllers.users.UsersApi;
import presidio.webapp.model.Alert;
import presidio.webapp.model.User;
import presidio.webapp.model.UsersWrapper;
import presidio.webapp.spring.OutputWebappConfiguration;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UserApiControllerModuleTest.springConfig.class)
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

    private ObjectMapper objectMapper;

    private presidio.output.domain.records.users.User user1;
    private presidio.output.domain.records.users.User user2;

    private Comparator<User> defaultUserComparator = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

    @Before
    public void setup() {
        //starting up the webapp server
        usersApiMVC = MockMvcBuilders.standaloneSetup(usersApi).setMessageConverters(mappingJackson2HttpMessageConverter).build();

        this.objectMapper = ObjectMapperProvider.customJsonObjectMapper();

        //save users in elastic
        user1 = generateUser(Arrays.asList("a"), "user1", "userId1", "user1", 50d);
        user2 = generateUser(Arrays.asList("b"), "user2", "userId2", "user2", 60d);
        List<presidio.output.domain.records.users.User> userList = Arrays.asList(user1, user2);
        Iterable<presidio.output.domain.records.users.User> savedUserd = userRepository.save(userList);
    }

    @After
    public void tearDown() {
        //delete the created users
        userRepository.delete(user1);
        userRepository.delete(user2);
    }

    @Test
    public void getAllUsers() throws Exception {

        // init expected response
        User expectedUser1 = convertDomainUserToRestUser(user1);
        User expectedUser2 = convertDomainUserToRestUser(user2);
        UsersWrapper expectedResponse = new UsersWrapper();
        expectedResponse.setTotal(2);
        List<User> users = Arrays.asList(expectedUser1, expectedUser2);
        users.sort(defaultUserComparator); //Sort the users list in order to be able to compare between expected and actual users list
        expectedResponse.setUsers(users);
        expectedResponse.setPage(0);

        // get actual response
        MvcResult mvcResult = usersApiMVC.perform(get(USERS_URI))
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



    private presidio.output.domain.records.users.User generateUser(List<String> classifications, String userName, String userId, String displayName, double score) {
        return new presidio.output.domain.records.users.User(userId, userName, displayName, score, classifications, null, new ArrayList<>(), UserSeverity.CRITICAL, 0);
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

    @Configuration
    @Import({OutputWebappConfiguration.class,RestTemplateConfig.class})
    public static class springConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer configurationApiControllerSpringTestPlaceholder() {
            Properties properties = new Properties();
            properties.put("default.page.size.for.rest.user", "1000");
            properties.put("default.page.number.for.rest.user", "1000");
            properties.put("default.page.size.for.rest.alert", "1000");
            properties.put("default.page.number.for.rest.alert", "1000");
            properties.put("elasticsearch.port", "9300");
            properties.put("elasticsearch.clustername", "fortscale");
            properties.put("elasticsearch.host", "dev-efratn");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}
