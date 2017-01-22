package fortscale.services;

import fortscale.domain.Exceptions.PasswordDecryptionException;
import fortscale.domain.ad.*;
import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;
import fortscale.services.impl.AdObjectType;

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
    Long getLatestRuntime(AdObject.AdObjectType adObjectType);
    Long countByTimestampepoch(AdObject.AdObjectType adObjectType, Long latestRuntime);
    Long getLastRunCount(AdObject.AdObjectType adObjectType);
    AdUserThumbnail findAdUserThumbnailById(String objectGUID);
    List<AdUserThumbnail> save(List<AdUserThumbnail> adUserThumbnails);

}
