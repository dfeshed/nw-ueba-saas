package fortscale.services;

import fortscale.domain.ad.AdConnection;
import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;

import java.io.BufferedWriter;
import java.util.List;

/**
 * Created by rafis on 16/05/16.
 */
public interface ActiveDirectoryService {

    void getFromActiveDirectory(BufferedWriter fileWriter, String filter, String
            adFields, int resultLimit, ActiveDirectoryResultHandler handler) throws Exception;

    List<AdConnection> getAdConnectionsFromDatabase();

    List<String> getDomainControllersFromDatabase();

    void saveDomainControllersInDatabase(List<String> domainControllers);
}
