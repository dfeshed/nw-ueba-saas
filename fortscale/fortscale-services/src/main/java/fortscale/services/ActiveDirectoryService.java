package fortscale.services;

import fortscale.domain.Exceptions.PasswordDecryptionException;
import fortscale.domain.ad.*;
import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;

import javax.naming.NamingException;
import java.util.List;

public interface ActiveDirectoryService {

    void getFromActiveDirectory(String filter, String adFields, int resultLimit, ActiveDirectoryResultHandler handler)
            throws Exception;
    List<AdConnection> getAdConnectionsFromDatabase();
    List<String> getDomainControllers();
    void saveDomainControllersInDatabase(List<String> domainControllers);
	void saveAdConnectionsInDatabase(List<AdConnection> adConnections);
    boolean canConnect(AdConnection adConnection) throws NamingException, PasswordDecryptionException;
    List<AdGroup> getGroupsByNameContains(String contains);
    List<AdOU> getOusByOuContains(String contains);
    Long getCount(AdObject.AdObjectType adObjectType);
    String getLatestRuntime(AdObject.AdObjectType adObjectType);
    Long countByRuntime(AdObject.AdObjectType adObjectType, String latestRuntime);
    Long getLastRunCount(AdObject.AdObjectType adObjectType);
    AdUserThumbnail findAdUserThumbnailById(String objectGUID);
    List<AdUserThumbnail> save(List<AdUserThumbnail> adUserThumbnails);

}
