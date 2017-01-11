package fortscale.services;

import fortscale.domain.Exceptions.PasswordDecryptionException;
import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.AdObject;
import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.NamingException;
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
    boolean canConnect(AdConnection adConnection) throws CommunicationException, AuthenticationException, NamingException, PasswordDecryptionException;
    List<AdGroup> getGroupsByNameContains(String contains);
    List<AdOU> getOusByOuContains(String contains);
    Long getCount(AdObject.AdObjectType adObjectType);
    Long getLatestRuntime(AdObject.AdObjectType adObjectType);
    Long countByTimestampepoch(AdObject.AdObjectType adObjectType, Long latestRuntime);
    Long getLastRunCount(AdObject.AdObjectType adObjectType);
}
