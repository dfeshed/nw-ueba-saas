package fortscale.ml.scorer.config;

import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConfServiceConfig;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.factory.config.ScorersFactoryConfig;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@Ignore
//todo: fix when asl stabilze
public class ProductionScorerConfFilesTest {
	private static final String NULL_SCORER_ERROR_MSG_FORMAT = "Received a null scorer for scorer conf %s.";

	@Autowired
	private ScorerConfService scorerConfService;
	@Autowired
	private FactoryService<Scorer> scorerFactoryService;

	@Test
	public void validate_all_scorer_confs() {
		for (DataSourceScorerConfs dataSourceScorerConfs : scorerConfService.getAllDataSourceScorerConfs().values()) {
			for (IScorerConf scorerConf : dataSourceScorerConfs.getScorerConfs()) {
				Scorer scorer = scorerFactoryService.getProduct(scorerConf);
				if (scorer == null) Assert.fail(String.format(NULL_SCORER_ERROR_MSG_FORMAT, scorerConf.getName()));
			}
		}
	}

	@Configuration
	@EnableSpringConfigured
	@Import({ScorersFactoryConfig.class, ModelConfServiceConfig.class})
	public static class spConf {
		@Autowired
		private List<AbstractServiceAutowiringFactory<Scorer>> scorersFactories;

		@Bean
		public ScorerConfService scorerConfService() {
			return new TestScorerConfService("classpath:config/asl/scorers/*/*.json");
		}

		@Bean
		public FactoryService<Scorer> scorerFactoryService() {
			FactoryService<Scorer> scorerFactoryService = new FactoryService<>();
			scorersFactories.forEach(x -> x.registerFactoryService(scorerFactoryService));
			return scorerFactoryService;
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

		@SuppressWarnings("unchecked")
		@Bean
		public FactoryService<IContextSelector> contextSelectorFactoryService() {
			return mock(FactoryService.class);
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
		public ModelsCacheService modelsCacheService() {
			return new ModelsCacheService() {
				@Override
				public Model getModel(String s, Map<String, String> m, Instant l) {return null;}

				@Override
				public void deleteFromCache(String modelConfName, String contextId) {}
			};
		}
	}
}
