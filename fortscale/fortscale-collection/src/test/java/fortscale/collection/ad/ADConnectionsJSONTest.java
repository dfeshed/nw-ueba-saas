package fortscale.collection.ad;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import fortscale.collection.jobs.ad.AdConnection;
import fortscale.collection.jobs.ad.AdConnections;
import java.util.List;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import org.junit.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@RunWith(JUnitParamsRunner.class)
public class ADConnectionsJSONTest {

	private String adConnectionsFile;

	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		adConnectionsFile = propertiesResolver.getProperty("ad.connections");
	}

	@Test
	public void adConnectionsTest() throws Exception {
		AdConnections adConnections = new AdConnections(adConnectionsFile);
		List<AdConnection> adConnectionList = adConnections.getAdConnections();
		assertFalse(adConnectionList.isEmpty());
		AdConnection adConnection = adConnectionList.get(0);
		assertFalse(adConnection.getDomainBaseSearch().isEmpty());
		assertFalse(adConnection.getDomainName().isEmpty());
		assertFalse(adConnection.getDomainPassword().isEmpty());
		assertFalse(adConnection.getDomainUser().isEmpty());
		List<String> ipAddresses = adConnection.getIpAddresses();
		assertFalse(ipAddresses.isEmpty());
		String ipAddress = ipAddresses.get(0);
		assertFalse(ipAddress.isEmpty());
	}

}