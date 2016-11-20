package fortscale.services;

import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.AdObject;
import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by rafis on 16/05/16.
 */
public interface ActiveDirectoryService {

    void getFromActiveDirectory(String filter, String adFields, int resultLimit, ActiveDirectoryResultHandler handler)
            throws Exception;
    List<AdConnection> getAdConnectionsFromDatabase();
    List<String> getDomainControllers();
    void saveDomainControllersInDatabase(List<String> domainControllers);
	void saveAdConnectionsInDatabase(List<AdConnection> adConnections);
    String canConnect(AdConnection adConnection);
    List<AdGroup> getGroupsByNameStartingWithIgnoreCase(String startsWith);
    List<AdOU> getOusByNameStartingWithIgnoreCase(String startsWith);
    Long getGroupsCount();
    Long getOusCount();
    Long getUserCount();
    Long getComputersCount();
    MongoRepository getRepository(AdObject.AdObjectType adObjectType);

}