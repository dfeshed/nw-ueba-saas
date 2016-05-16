package fortscale.collection.ad;

import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.AdConnections;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertFalse;

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
