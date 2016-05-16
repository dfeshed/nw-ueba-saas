package fortscale.services;

import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;

import java.io.BufferedWriter;

/**
 * Created by rafis on 16/05/16.
 */
public interface ActiveDirectoryService {

    void fetchFromActiveDirectory(BufferedWriter fileWriter, String _filter, String
            _adFields, int resultLimit, ActiveDirectoryResultHandler handler) throws Exception;

}
