package presidio.ade.modeling.config;

import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.smart.record.conf.SmartRecordConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lior Govrin
 */
@Configuration
public class ModelingServiceSmartRecordConfServiceConfig {
    @Value("${presidio.ade.smart.record.base.configurations.path}")
    private String smartRecordBaseConfigurationsPath;
    @Value("${presidio.ade.smart.record.overriding.configurations.path:#{null}}")
    private String smartRecordOverridingConfigurationsPath;
    @Value("${presidio.ade.smart.record.additional.configurations.path:#{null}}")
    private String smartRecordAdditionalConfigurationsPath;
    @Autowired
    public AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Bean
    public SmartRecordConfService smartRecordConfService() {
        return new SmartRecordConfService(
                smartRecordBaseConfigurationsPath,
                smartRecordOverridingConfigurationsPath,
                smartRecordAdditionalConfigurationsPath,
                aggregatedFeatureEventsConfService);
    }
}
