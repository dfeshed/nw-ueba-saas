package fortscale.collection.ad;

import com.google.common.io.Resources;
import org.junit.Test;
import fortscale.collection.jobs.ad.AdConnection;
import fortscale.collection.jobs.ad.AdConnections;

import java.net.URL;
import java.util.List;
import static org.junit.Assert.*;

public class ADConnectionsJSONTest {

	@Test
	public void checkJacksonMappingOfADConnections() throws Exception {
		URL url = Resources.getResource("adConnectionsTest.json");
		AdConnections adConnections = new AdConnections(url.getFile());
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
