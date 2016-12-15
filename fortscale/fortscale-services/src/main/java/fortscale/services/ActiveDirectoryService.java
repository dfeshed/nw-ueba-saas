package fortscale.services;

import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.AdObject;
import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;

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
    List<AdGroup> getGroupsByNameContains(String contains);
    List<AdOU> getOusByOuContains(String contains);
    Long getCount(AdObject.AdObjectType adObjectType);

}
