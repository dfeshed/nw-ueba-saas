package fortscale.domain;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/fortscale-domain-context-test.xml" })
public abstract class AbstractIntegrationTest {

	@Autowired
	MongoDbFactory mongoDbFactory;

	@Before
	public void setUp() {

//		DB database = mongoDbFactory.getDb();

		// Customers

//		DBCollection users = database.getCollection("user");
//		users.remove(new BasicDBObject());

//		BasicDBObject address = new BasicDBObject();
//		address.put("city", "New York");
//		address.put("street", "Broadway");
//		address.put("country", "United States");
//
//		BasicDBList addresses = new BasicDBList();
//		addresses.add(address);

//		DBObject dave = new BasicDBObject("username", "dmatthews");
//		dave.put("firstname", "Dave");
//		dave.put("lastname", "Matthews");
//		dave.put("adDn", "dn:dmatthews");
//		dave.put("testingUnknownField", "test");
//		dave.put("email", "dave@dmband.com");
////		dave.put("addresses", addresses);
//
//		users.insert(dave);

	}
}
