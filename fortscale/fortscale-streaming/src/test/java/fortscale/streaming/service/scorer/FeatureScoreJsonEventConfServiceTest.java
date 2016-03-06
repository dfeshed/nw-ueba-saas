package fortscale.streaming.service.scorer;

import fortscale.ml.scorer.config.DataSourceScorerConfs;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.config.ScorerContainerConf;
import fortscale.streaming.service.FortscaleValueResolver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class FeatureScoreJsonEventConfServiceTest {
	private static final String DUPLICATE_SCORER_CONF_NAME_FORMAT = "Duplicate scorer conf name: %s.";
	private static final String NO_SUCH_SCORER_CONF_FORMAT = "Scorer conf named %s does not exist.";

	@Configuration
	static class ContextConfiguration {
		@Bean
		public ScorerConfService scorerConfService() {
			return new ScorerConfService() {
				@Override
				protected String getBaseConfJsonFilesPath() {
					return "classpath:config/asl/scorers/*/*.json";
				}

				@Override
				protected String getBaseOverridingConfJsonFolderPath() {
					return null;
				}

				@Override
				protected String getAdditionalConfJsonFolderPath() {
					return null;
				}
			};
		}

		@Bean
		public FeatureScoreJsonEventConfService featureScoreJsonEventConfService() {
			return new FeatureScoreJsonEventConfService();
		}

		@Bean
		public FortscaleValueResolver fortscaleValueResolver() {
			FortscaleValueResolver fortscaleValueResolver = mock(FortscaleValueResolver.class);
			when(fortscaleValueResolver.resolveStringValue(anyString()))
					.thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
			return fortscaleValueResolver;
		}

		@Bean
		public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			// The overriding file path cannot be null, so the base file path is copied
			Properties properties = new Properties();
			properties.put("fortscale.streaming.scores.to.event.mapping.conf.json.file.path",
					"classpath:scorers/scores_to_event_map.json");
			properties.put("fortscale.streaming.scores.to.event.mapping.conf.json.overriding.file.path",
					"classpath:scorers/scores_to_event_map.json");
			PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
			configurer.setProperties(properties);
			return configurer;
		}
	}

	@Autowired
	private ScorerConfService scorerConfService;
	@Autowired
	private FeatureScoreJsonEventConfService featureScoreJsonEventConfService;

	@Test
	public void validateScoresToEventMapJson() {
		Map<String, IScorerConf> nameToScorerConf = new HashMap<>();

		for (DataSourceScorerConfs dataSourceScorerConfs : scorerConfService.getAllDataSourceScorerConfs().values()) {
			for (IScorerConf scorerConf : dataSourceScorerConfs.getScorerConfs()) {
				if (nameToScorerConf.containsKey(scorerConf.getName()))
					Assert.fail(String.format(DUPLICATE_SCORER_CONF_NAME_FORMAT, scorerConf.getName()));
				else
					nameToScorerConf.put(scorerConf.getName(), scorerConf);
			}
		}

		int counter = 0;

		for (List<String> scorerNamePath : featureScoreJsonEventConfService.getAllScorerNamePaths()) {
			String scorerConfName = scorerNamePath.get(0);
			IScorerConf scorerConf = nameToScorerConf.get(scorerConfName);
			if (scorerConf == null)
				Assert.fail(String.format(NO_SUCH_SCORER_CONF_FORMAT, scorerConfName));
			else
				counter++;

			for (int i = 1; i < scorerNamePath.size(); i++) {
				// Assume scorer conf is of type ScorerContainerConf
				List<IScorerConf> scorerConfs = ((ScorerContainerConf)scorerConf).getScorerConfList();
				scorerConfName = scorerNamePath.get(i);
				int nextScorerConfIndex = indexOf(scorerConfs, scorerConfName);

				if (nextScorerConfIndex == -1)
					Assert.fail(String.format(NO_SUCH_SCORER_CONF_FORMAT, scorerConfName));
				else
					scorerConf = scorerConfs.get(nextScorerConfIndex);
			}
		}

		Assert.assertEquals(115, counter);
	}

	private static int indexOf(List<IScorerConf> scorerConfs, String scorerConfName) {
		for (int i = 0; i < scorerConfs.size(); i++) {
			if (scorerConfs.get(i).getName().equals(scorerConfName)) {
				return i;
			}
		}

		return -1;
	}
}
