package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureValue;
import fortscale.common.feature.extraction.FeatureExtractService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.hadoop.config.common.annotation.EnableAnnotationConfiguration;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@EnableAnnotationConfiguration
@EnableSpringConfigured
public class ScorerTestsContext {
	@Bean
	public FeatureExtractService featureExtractService() {
		FeatureExtractService featureExtractService = mock(FeatureExtractService.class);
		when(featureExtractService.extract(anyString(), any(Event.class))).then(invocationOnMock -> {
			String featureName = (String)invocationOnMock.getArguments()[0];
			Event event = (Event)invocationOnMock.getArguments()[1];
			return getFeature(featureName, event);
		});
		return featureExtractService;
	}

	private static Feature getFeature(String featureName, Event event) {
		Object featureValue = event.get(featureName);
		if (featureValue == null) {
			return new Feature(featureName, (FeatureValue)null);
		} else if (featureValue instanceof Number) {
			return new Feature(featureName, (Number)featureValue);
		} else {
			return new Feature(featureName, featureValue.toString());
		}
	}
}
