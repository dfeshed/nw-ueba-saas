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
		properties.put("presidio.ade.modeling.config.asl.path", "classpath*:config/asl/modeling_service");
		// Feature bucket conf service
		properties.put("presidio.ade.modeling.feature.bucket.confs.base.path", "${presidio.ade.modeling.config.asl.path}/feature_buckets/*.json");
		// Feature aggregation event conf service
		properties.put("presidio.ade.modeling.feature.aggregation.event.confs.base.path", "${presidio.ade.modeling.config.asl.path}/feature_aggregation_events/*.json");
		// Smart event conf service
		properties.put("fortscale.entity.event.definitions.json.file.path", "classpath*:config/asl/smart-records/*.json");
		// Model conf service
		properties.put("presidio.ade.modeling.enriched.records.group.name", "enriched-record-models");
		properties.put("presidio.ade.modeling.enriched.records.base.configuration.path", "${presidio.ade.modeling.config.asl.path}/models/enriched_records/*.json");
		properties.put("presidio.ade.modeling.feature.aggregation.records.group.name", "feature-aggregation-record-models");
		properties.put("presidio.ade.modeling.feature.aggregation.records.base.configuration.path", "${presidio.ade.modeling.config.asl.path}/models/feature_aggregation_records/*.json");
		properties.put("presidio.ade.modeling.smart.records.group.name", "smart-record-models");
		properties.put("presidio.ade.modeling.smart.records.base.configuration.path", "${presidio.ade.modeling.config.asl.path}/models/smart_records/*.json");
		// Additional properties
		properties.put("presidio.ade.modeling.feature.buckets.default.expire.after.seconds", 7776000);
		properties.put("presidio.ade.modeling.event.type.field.value.aggr.event", "aggr_event");
		properties.put("presidio.ade.modeling.event.type.field.value.entity.event", "entity_event");
		properties.put("presidio.ade.modeling.context.field.key", "context");
		properties.put("entity.event.data.cache.reader.service.max.cache.size", 0);
		properties.put("entity.event.data.cache.reader.service.time.to.expire.seconds", 259200);
		properties.put("fortscale.scored.entity.event.store.page.size", 10000);
		return new TestPropertiesPlaceholderConfigurer(properties);
	}
}
