package fortscale.utils.airflow.service;

import fortscale.utils.RestTemplateConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

/**
 * spring config for {@link AirflowApiClient}
 * Created by barak_schuster on 9/14/17.
 */
@Configuration
@Import(RestTemplateConfig.class)
public class AirflowApiClientConfig {
    @Value("${presidio.airflow.restApi.url:http://localhost:8000/admin/rest_api/api}")
    private String airflowRestApiBaseUrl;
    @Autowired
    public RestTemplate restTemplate;


    @Bean
    public AirflowApiClient airflowApiClient()
    {
        return new AirflowApiClientImpl(restTemplate,airflowRestApiBaseUrl);
    }


}
