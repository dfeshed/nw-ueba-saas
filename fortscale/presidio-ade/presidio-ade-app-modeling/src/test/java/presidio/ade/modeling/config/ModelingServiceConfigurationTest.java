package presidio.ade.modeling.config;

import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;

/**
 * @author Lior Govrin
 */
@Configuration
@Import({ModelingServiceConfiguration.class, MongodbTestConfig.class, BootShimConfig.class})
public class ModelingServiceConfigurationTest {
	@Bean
	public static TestPropertiesPlaceholderConfigurer modelingServiceConfigurationTestPropertiesPlaceholderConfigurer() {
		Properties properties = new Properties();
		// Feature bucket conf service
		properties.put("presidio.ade.modeling.feature.bucket.confs.base.path", "classpath*:config/asl/feature-buckets/**/*.json");
		// Feature aggregation event conf service
		properties.put("presidio.ade.modeling.feature.aggregation.event.confs.base.path", "classpath*:config/asl/aggregation-records/feature-aggregation-records/*.json");
		// Smart event conf service
		properties.put("presidio.ade.smart.record.base.configurations.path","classpath*:config/asl/smart-records/*");
		// Model conf service
		properties.put("presidio.ade.modeling.enriched.records.group.name", "enriched-record-models");
		properties.put("presidio.ade.modeling.enriched.records.base.configuration.path", "classpath:config/asl/models/enriched-record-test/");
		properties.put("presidio.ade.modeling.feature.aggregation.records.group.name", "feature-aggregation-record-models");
		properties.put("presidio.ade.modeling.feature.aggregation.records.base.configuration.path", "classpath*:config/asl/models/feature-aggregation-records/");
		properties.put("presidio.ade.modeling.smart.records.group.name", "smart-record-models");
		properties.put("presidio.ade.modeling.smart.records.base.configuration.path", "classpath*:config/asl/models/smart-records/");
		// Additional properties
		properties.put("presidio.ade.modeling.feature.buckets.default.expire.after.seconds", 7776000);
		properties.put("fortscale.model.retriever.smart.oldestAllowedModelDurationDiff","PT48H");
		return new TestPropertiesPlaceholderConfigurer(properties);
	}
}
