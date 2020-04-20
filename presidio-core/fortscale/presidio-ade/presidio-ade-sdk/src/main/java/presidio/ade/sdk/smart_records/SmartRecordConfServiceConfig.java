package presidio.ade.sdk.smart_records;

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
public class SmartRecordConfServiceConfig {
	@Value("${fortscale.ademanager.smart.record.base.configurations.path:classpath:config/asl/smart-records/*.json}")
	private String smartRecordBaseConfigurationsPath;
	@Value("${fortscale.ademanager.smart.record.overriding.configurations.path:#{null}}")
	private String smartRecordOverridingConfigurationsPath;
	@Value("${fortscale.ademanager.smart.record.additional.configurations.path:#{null}}")
	private String smartRecordAdditionalConfigurationsPath;

	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

	@Bean
	public SmartRecordConfService smartRecordConfService() {
		return new SmartRecordConfService(
				smartRecordBaseConfigurationsPath,
				smartRecordOverridingConfigurationsPath,
				smartRecordAdditionalConfigurationsPath,
				aggregatedFeatureEventsConfService);
	}
}
