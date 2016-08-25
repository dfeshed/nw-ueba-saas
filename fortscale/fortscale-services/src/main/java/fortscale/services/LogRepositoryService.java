package fortscale.services;

import fortscale.domain.fetch.LogRepository;

import java.util.List;

/**
 * Created by Amir Keren on 15/08/16.
 */
public interface LogRepositoryService {

    List<LogRepository> getLogRepositoriesFromDatabase();
    void saveLogRepositoriesInDatabase(List<LogRepository> logRepositories);
    String canConnect(LogRepository logRepository);

}