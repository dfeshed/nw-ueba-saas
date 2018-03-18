package fortscale.web.demo.services;


import fortscale.domain.core.Alert;
import fortscale.domain.core.DataSourceAnomalyTypePair;
import fortscale.domain.core.FavoriteUserFilter;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.FavoriteUserFilterRepository;
import fortscale.domain.rest.UserFilter;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.UserService;
import fortscale.temp.HardCodedMocks;
import fortscale.utils.logging.Logger;
import fortscale.web.demoservices.DemoBuilder;
import fortscale.web.demoservices.services.MockDemoUserServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(MockitoJUnitRunner.class)
public class MockDemoUserServiceImplTest {

	private MockDemoUserServiceImpl mockDemoUserService;
	private DemoBuilder demoBuilder;
	@Mock
	private FavoriteUserFilterRepository favoriteUserFilterRepository;

	@Before
	public void setUp(){

		User user1=new User();
		user1.setMockId("1");
		user1.setSearchField("Mark Avraham");
		user1.setUsername("Mark@Avraham");
		user1.setDisplayName("Mark Avraham");
		user1.setFollowed(true);
		user1.setScore(10);


		User user2=new User();
		user2.setMockId("2");
		user2.setSearchField("Beni Burger");
		user2.setUsername("Beni@Burger");
		user2.setDisplayName("Beni Burger");
		user2.setFollowed(true);
		user2.setScore(30);

		User user3=new User();
		user3.setMockId("3");
		user3.setUsername("Moshe@Burger");
		user3.setDisplayName("Moshe Burger");
		user3.setFollowed(false);
		user3.setScore(40);

		User user4=new User();
		user4.setMockId("4");
		user4.setUsername("Avi@Burger");
		user4.setDisplayName("Avi Burger");
		user4.setFollowed(true);
		user4.setScore(50);

		List<User> users = new ArrayList<>();
		users.add(user1);
		users.add(user2);
		users.add(user3);
		users.add(user4);

		 demoBuilder = new DemoBuilder(users,new ArrayList<>(),null);
		 mockDemoUserService = new MockDemoUserServiceImpl(favoriteUserFilterRepository,demoBuilder);


	}



	@Test
	public void testFindUsersById(){
		List<User> foundUsers = mockDemoUserService.findByIds(Arrays.asList("1","3"));
		Assert.assertEquals(2, foundUsers.size());
		Assert.assertEquals("1", foundUsers.get(0).getId());
		Assert.assertEquals("3", foundUsers.get(1).getId());

	}

	@Test
	public void testfindBySearchListNoPageSize(){
		List<User> foundUsers = mockDemoUserService.findBySearchFieldContaining("mark",1,5);
		Assert.assertEquals(1, foundUsers.size());
		Assert.assertEquals("1", foundUsers.get(0).getId());
	}

	@Test
	public void testfindBySearchListNoResults(){
		List<User> foundUsers = mockDemoUserService.findBySearchFieldContaining("ark",1,5);
		Assert.assertEquals(0, foundUsers.size());
	}


	@Test
	public void testfindBySearchListWithPageSize(){
		demoBuilder.getUsers().clear();;

		User user1=new User();
		user1.setMockId("0");
		user1.setSearchField("Avi Hola");
		demoBuilder.getUsers().add(user1);


		user1=new User();
		user1.setMockId("1");
		user1.setSearchField("Mark Avraham");
		demoBuilder.getUsers().add(user1);

		user1=new User();
		user1.setMockId("2");
		user1.setSearchField("Mark Avraham");
		demoBuilder.getUsers().add(user1);

		user1=new User();
		user1.setMockId("3");
		user1.setSearchField("Mark Avraham");
		demoBuilder.getUsers().add(user1);

		user1=new User();
		user1.setMockId("4");
		user1.setSearchField("Mark Avraham");
		demoBuilder.getUsers().add(user1);

		user1=new User();
		user1.setMockId("5");
		user1.setSearchField("Mark Avraham");
		demoBuilder.getUsers().add(user1);

		user1=new User();
		user1.setMockId("6");
		user1.setSearchField("Mark Avraham");
		demoBuilder.getUsers().add(user1);

		user1=new User();
		user1.setMockId("7");
		user1.setSearchField("Mark Avraham");
		demoBuilder.getUsers().add(user1);

		user1=new User();
		user1.setMockId("8");
		user1.setSearchField("Mark Avraham");
		demoBuilder.getUsers().add(user1);

		user1=new User();
		user1.setMockId("9");
		user1.setSearchField("Mark Avraham");
		demoBuilder.getUsers().add(user1);

		user1=new User();
		user1.setMockId("10");
		user1.setSearchField("Avi Avraham");
		demoBuilder.getUsers().add(user1);

		user1=new User();
		user1.setMockId("11");
		user1.setSearchField("Moshe Avraham");
		demoBuilder.getUsers().add(user1);



		List<User> foundUsers = mockDemoUserService.findBySearchFieldContaining("mark",1,5);
		Assert.assertEquals(5, foundUsers.size());
		Assert.assertEquals("1", foundUsers.get(0).getId());
		Assert.assertEquals("2", foundUsers.get(1).getId());
		Assert.assertEquals("3", foundUsers.get(2).getId());
		Assert.assertEquals("4", foundUsers.get(3).getId());
		Assert.assertEquals("5", foundUsers.get(4).getId());
	}

	@Test
	public void testFindByUserName(){

        User user = mockDemoUserService.findByUsername("Mark@Avraham");
        Assert.assertNotNull(user);
        Assert.assertEquals("1",user.getId());

//		int count = mockDemoUserService.countUsersByDisplayName(new HashSet<>(Arrays.asList("Avi Burger")));

	}

    @Test
    public void testFindByNotExistingUserName(){

        User user = mockDemoUserService.findByUsername("Mark@Avraham2");
        Assert.assertNull(user);


    }


	@Test
	public void testCountUsersByFilterSearchValue(){
		UserRestFilter userRestFilter = new UserRestFilter();
		userRestFilter.setSearchValue("Beni");
		Set<String> relevantUsers = new HashSet<>();

		int count = mockDemoUserService.countUsersByFilter(userRestFilter,relevantUsers);

		Assert.assertEquals(1,count);


	}

	@Test
	public void testCountUsersByAlertTypes(){

		Alert a1 = new Alert();
		a1.setName("type1");
		a1.setEntityId("101");
		a1.setEntityName("alert@type1.com");

		Alert a0 = new Alert();
		a0.setName("type1");
		a0.setEntityId("101");
		a0.setEntityName("alert@type1.com");

		Alert a2 = new Alert();
		a2.setName("type2");
		a2.setEntityId("101");
		a2.setEntityName("alert@type1.com");

		Alert a3 = new Alert();
		a3.setName("type2");
		a3.setEntityId("202");
		a3.setEntityName("alert@type2.com");

		Alert a4 = new Alert();
		a4.setName("type3");
		a4.setEntityId("303");
		a4.setEntityName("alert@type3.com");

		demoBuilder.getAlerts().add(a1);
		demoBuilder.getAlerts().add(a2);
		demoBuilder.getAlerts().add(a0);
		demoBuilder.getAlerts().add(a3);
		demoBuilder.getAlerts().add(a4);

		User user1=new User();
		user1.setMockId("101");
		user1.setSearchField("AlertType1");
		user1.setUsername("alert@type1.com");

		User user2=new User();
		user2.setMockId("202");
		user2.setSearchField("AlertType2");
		user2.setUsername("alert@type2.com");

		User user3=new User();
		user3.setMockId("303");
		user3.setSearchField("AlertType3");
		user3.setUsername("alert@type3.com");

		demoBuilder.getUsers().add(user1);
		demoBuilder.getUsers().add(user2);
		demoBuilder.getUsers().add(user3);

		UserRestFilter userRestFilter = new UserRestFilter();
		userRestFilter.setAlertTypes(Arrays.asList("type1","type2"));

		Set<String> relevantUsers = new HashSet<>();

		int count = mockDemoUserService.countUsersByFilter(userRestFilter,relevantUsers);

		Assert.assertEquals(2,count); //2 users with alerts of type1, types2

		userRestFilter = new UserRestFilter();
		userRestFilter.setAlertTypes(Arrays.asList("type3"));



		count = mockDemoUserService.countUsersByFilter(userRestFilter,relevantUsers);

		Assert.assertEquals(1,count); //1 user with alerts of type3


	}

	@Test
	public void testFindOne(){
		User u=mockDemoUserService.findOne("1");
		Assert.assertNotNull(u);
		Assert.assertEquals("Mark@Avraham",u.getUsername());
	}
	@Test
	public void testFindOneNotExisting(){
		User u=mockDemoUserService.findOne("1000");
		Assert.assertNull(u);

	}

	@Test
	public void testFindWatched(){
		Set<User> users =mockDemoUserService.findByFollowed();
		Assert.assertNotNull(users);
		Assert.assertEquals(3,users.size());
	}

	@Test
	public void findUsersByFilterEntityMinScore(){
		UserRestFilter filter = new UserRestFilter();
		filter.setEntityMinScore(20);
		List<User> users = mockDemoUserService.findUsersByFilter(filter, new PageRequest(0,10),null,null,true).getUsers();
		Assert.assertEquals(3,users.size());
	}

	@Test
	public void findUsersByAlertTypes(){
		User user1 = demoBuilder.getUsers().get(0);
		User user2 = demoBuilder.getUsers().get(1);

		Alert a1 = new Alert();
		a1.setName("alertType1");
		a1.setEntityName(user1.getUsername());
		a1.setEntityId(user1.getId());

		Alert a2 = new Alert();
		a2.setName("alertType2");
		a2.setEntityName(user1.getUsername());
		a2.setEntityId(user1.getId());

		Alert a3 = new Alert();
		a3.setName("alertType1");
		a3.setEntityName(user2.getUsername());
		a3.setEntityId(user2.getId());

		demoBuilder.getAlerts().add(a1);
		demoBuilder.getAlerts().add(a2);
		demoBuilder.getAlerts().add(a3);

		UserRestFilter filter = new UserRestFilter();
		filter.setAlertTypes(Arrays.asList("alertType1"));
		List<User> users = mockDemoUserService.findUsersByFilter(filter, new PageRequest(0,10),null,null,true).getUsers();
		Assert.assertEquals(2,users.size());
	}

	@Test
	public void findUsersByAlertTypesNoResults(){
		User user1 = demoBuilder.getUsers().get(0);
		User user2 = demoBuilder.getUsers().get(1);

		Alert a1 = new Alert();
		a1.setName("alertType1");
		a1.setEntityName(user1.getUsername());
		a1.setEntityId(user1.getId());

		Alert a2 = new Alert();
		a2.setName("alertType2");
		a2.setEntityName(user1.getUsername());
		a2.setEntityId(user1.getId());

		Alert a3 = new Alert();
		a3.setName("alertType1");
		a3.setEntityName(user2.getUsername());
		a3.setEntityId(user2.getId());

		demoBuilder.getAlerts().add(a1);
		demoBuilder.getAlerts().add(a2);
		demoBuilder.getAlerts().add(a3);

		UserRestFilter filter = new UserRestFilter();
		filter.setAlertTypes(Arrays.asList("alertType4"));
		List<User> users = mockDemoUserService.findUsersByFilter(filter, new PageRequest(0,10),null,null,true).getUsers();
		Assert.assertEquals(0,users.size());
	}

	@Test
	public void findUsersByWatchedUser(){
		UserRestFilter filter = new UserRestFilter();
		filter.setIsWatched(true);
		List<User> users = mockDemoUserService.findUsersByFilter(filter, new PageRequest(0,10),null,null,true).getUsers();
		Assert.assertEquals(3,users.size());
	}


}
