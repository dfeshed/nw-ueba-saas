package presidio.manager.api.service;

import fortscale.utils.airflow.service.AirflowApiClient;
import fortscale.utils.airflow.service.AirflowApiClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Created by barak_schuster on 9/18/17.
 */
@Configuration
@Import(AirflowApiClientConfig.class)
public class ManagerServiceConfig {

    @Autowired
    private AirflowApiClient airflowApiClient;
    private List<String> managerDagIds;
    private Instant dataPipelineStartTime;
    private Duration buildingBaselineDuration;

    @Bean
    public ManagerService ManagerServiceImpl()
    {
        return new ManagerServiceImpl(managerDagIds,airflowApiClient,dataPipelineStartTime,buildingBaselineDuration);
    }
}
