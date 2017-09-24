package presidio.manager.api.service;

import fortscale.utils.airflow.service.AirflowApiClient;
import fortscale.utils.airflow.service.AirflowApiClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;

/**
 * Created by barak_schuster on 9/18/17.
 */
@Configuration
@Import(AirflowApiClientConfig.class)
public class ManagerServiceConfig {

    @Autowired
    private AirflowApiClient airflowApiClient;

    @Value("${manager.dags.dag_id.fullFlow.prefix}")
    private String managerDagIdPrefix;

    @Value("#{T(java.time.Duration).parse('${manager.dags.state.buildingBaselineDuration}')}")
    private Duration buildingBaselineDuration;

    @Bean
    public ManagerService ManagerServiceImpl()
    {
        return new ManagerServiceImpl(managerDagIdPrefix,airflowApiClient, buildingBaselineDuration);
    }
}
