package fortscale.ml.scorer.config;

import fortscale.common.feature.Feature;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.scorer.Scorer;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.hadoop.config.common.annotation.EnableAnnotationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ProductionScorerConfFilesTest {
	private static final String NULL_SCORER_ERROR_MSG_FORMAT = "Received a null scorer for scorer conf %s.";

	@Autowired
	private ScorerConfService scorerConfService;
	@Autowired
	private FactoryService<Scorer> scorerFactoryService;

	@Test
	public void validate_all_scorer_confs() {
		int expRawScorerConfs = 11;
		int expAggrScorerConfs = 64;
		int expEntityScorerConfs = 2;
		int actualScorerConfs = 0;

		for (DataSourceScorerConfs dataSourceScorerConfs : scorerConfService.getAllDataSourceScorerConfs().values()) {
			for (IScorerConf scorerConf : dataSourceScorerConfs.getScorerConfs()) {
				Scorer scorer = scorerFactoryService.getProduct(scorerConf);
				if (scorer == null) Assert.fail(String.format(NULL_SCORER_ERROR_MSG_FORMAT, scorerConf.getName()));
				else actualScorerConfs++;
			}
		}

		Assert.assertEquals(expRawScorerConfs + expAggrScorerConfs + expEntityScorerConfs, actualScorerConfs);
	}

	@Test
	public void get_kerberos_logins_data_source_scorer_confs() {
		getDataSourceScorerConfs("kerberos_logins", Collections.singletonList(4));
	}

	@Test
	public void get_ssh_data_source_scorer_confs() {
		getDataSourceScorerConfs("ssh", Collections.singletonList(4));
	}

	@Test
	public void get_vpn_data_source_scorer_confs() {
		getDataSourceScorerConfs("vpn", Collections.singletonList(3));
	}

	@Test
	public void get_vpn_session_data_source_scorer_confs() {
		getDataSourceScorerConfs("vpn_session", Collections.singletonList(3));
	}

	private void getDataSourceScorerConfs(String dataSource, List<Integer> sizes) {
		DataSourceScorerConfs dataSourceScorerConfs = scorerConfService.getDataSourceScorerConfs(dataSource);

		if (dataSourceScorerConfs == null) {
			Assert.fail(String.format("Received null %s for data source %s.",
					DataSourceScorerConfs.class.getSimpleName(), dataSource));
		}

		Assert.assertEquals(dataSource, dataSourceScorerConfs.getDataSource());
		Assert.assertEquals(sizes.size(), dataSourceScorerConfs.getScorerConfs().size());

		for (int i = 0; i < sizes.size(); i++) {
			ScorerContainerConf conf = (ScorerContainerConf)dataSourceScorerConfs.getScorerConfs().get(i);
			Assert.assertEquals(sizes.get(i), conf.getScorerConfList().size(), 0d);
		}
	}

	@Configuration
	@EnableSpringConfigured
	@EnableAnnotationConfiguration
	@ComponentScan(basePackages = "fortscale.ml.scorer.factory")
	static class ContextConfiguration {
		@Bean
		public ModelConfService modelConfService() {
			return new ModelConfService();
		}

		@Bean
		public ScorerConfService scorerConfService() {
			return new TestScorerConfService("classpath:config/asl/scorers/*/*.json");
		}

		@Bean
		public FactoryService<Scorer> scorerFactoryService() {
			return new FactoryService<>();
		}

		@Bean
		public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			Properties properties = new Properties();
			properties.put("fortscale.model.configurations.location.path", "classpath:config/asl/models");
			PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
			configurer.setProperties(properties);
			return configurer;
		}

		@SuppressWarnings("unchecked")
		@Bean
		public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
			AbstractDataRetriever dataRetriever = mock(AbstractDataRetriever.class);
			when(dataRetriever.getEventFeatureNames()).thenReturn(Collections.singleton("myEventFeature"));
			when(dataRetriever.getContextFieldNames()).thenReturn(Collections.singletonList("myContextField"));
			FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = mock(FactoryService.class);
			when(dataRetrieverFactoryService.getProduct(any(AbstractDataRetrieverConf.class))).thenReturn(dataRetriever);
			return dataRetrieverFactoryService;
		}

		/**
		 * Following beans are required and autowired,
		 * but they aren't used in the tests' flow.
		 * These beans should be mocks.
		 */

		@Bean
		public EventModelsCacheService eventModelsCacheService() {
			return new EventModelsCacheService();
		}

		@Bean
		public FeatureExtractService featureExtractService() {
			return new FeatureExtractService();
		}

		@Bean
		public ModelsCacheService modelsCacheService() {
			return new ModelsCacheService() {
				@Override
				public Model getModel(Feature f, String s, Map<String, Feature> m, long l) {return null;}

				@Override
				public void window() {}

				@Override
				public void close() {}
			};
		}
	}
}
