package fortscale.services.cloudera;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Created by barak_schuster on 1/17/17.
 */
@Configuration
public class ClouderaServiceConfig {
    @Value("${cloudera_manager_host}")
    private String serverHost;
    @Value("${cm_cluster_name}")
    private String clusterName;
    @Value("${cm_user}")
    private String cmAdminUser;
    @Value("${cm_pass}")
    private String cmAdminPass;
    @Value("#{ T(java.time.Duration).parse('${cm_role_start_timeout_after}')}")
    private Duration startTimeout;
    @Value("#{ T(java.time.Duration).parse('${cm_role_stop_timeout_after}')}")
    private Duration stopTimeout;

    @Bean
    public ClouderaService clouderaService()
    {
        ClouderaServiceImpl clouderaService = new ClouderaServiceImpl(serverHost, clusterName, cmAdminUser, cmAdminPass, startTimeout, stopTimeout);
        clouderaService.init();
        return clouderaService;
    }
}
