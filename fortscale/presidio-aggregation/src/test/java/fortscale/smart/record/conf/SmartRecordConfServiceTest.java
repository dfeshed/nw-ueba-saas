package fortscale.smart.record.conf;

import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.pagination.aggregated.AggregatedDataPaginationParam;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Lior Govrin
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class SmartRecordConfServiceTest {
	@Autowired
	private SmartRecordConfService smartRecordConfService;

	@Test
	public void service_should_deserialize_smart_record_confs_from_json_file_and_complete_cluster_confs() {
		List<SmartRecordConf> smartRecordConfs = smartRecordConfService.getSmartRecordConfs().stream()
				.sorted(comparing(SmartRecordConf::getName))
				.collect(Collectors.toList());

		assertEquals(2, smartRecordConfs.size());
		assertFirstSmartRecordConf(smartRecordConfs.get(0));
		assertSecondSmartRecordConf(smartRecordConfs.get(1));
	}

	@Test
	public void test_get_smart_record_conf() {
		assertFirstSmartRecordConf(smartRecordConfService.getSmartRecordConf("test_smart_record_conf_1"));
	}

	@Test
	public void test_get_pagination_params() {
		List<AggregatedDataPaginationParam> paginationParams = smartRecordConfService.getPaginationParams("test_smart_record_conf_2").stream()
				.sorted(comparing(AggregatedDataPaginationParam::getFeatureName))
				.collect(Collectors.toList());
		assertEquals(4, paginationParams.size());

		AggregatedDataPaginationParam paginationParam = paginationParams.get(0);
		assertEquals("featureAggregationRecord1", paginationParam.getFeatureName());
		assertEquals(AggregatedFeatureType.FEATURE_AGGREGATION, paginationParam.getAggregatedFeatureType());

		paginationParam = paginationParams.get(1);
		assertEquals("featureAggregationRecord2", paginationParam.getFeatureName());
		assertEquals(AggregatedFeatureType.FEATURE_AGGREGATION, paginationParam.getAggregatedFeatureType());

		paginationParam = paginationParams.get(2);
		assertEquals("scoreAggregationRecord1", paginationParam.getFeatureName());
		assertEquals(AggregatedFeatureType.SCORE_AGGREGATION, paginationParam.getAggregatedFeatureType());

		paginationParam = paginationParams.get(3);
		assertEquals("scoreAggregationRecord2", paginationParam.getFeatureName());
		assertEquals(AggregatedFeatureType.SCORE_AGGREGATION, paginationParam.getAggregatedFeatureType());
	}

	private void assertFirstSmartRecordConf(SmartRecordConf smartRecordConf) {
		assertEquals("test_smart_record_conf_1", smartRecordConf.getName());
		assertEquals(singletonList("userId"), smartRecordConf.getContexts());
		assertEquals(FixedDurationStrategy.HOURLY, smartRecordConf.getFixedDurationStrategy());
		assertEquals(true, smartRecordConf.isIncludeAllAggregationRecords());
		assertEquals(0.5, smartRecordConf.getDefaultWeight(), 0);
		List<ClusterConf> clusterConfs = smartRecordConf.getClusterConfs().stream()
				.sorted(comparing(clusterConf -> clusterConf.getAggregationRecordNames().get(0)))
				.collect(Collectors.toList());
		assertEquals(4, clusterConfs.size());

		ClusterConf clusterConf = clusterConfs.get(0);
		List<String> aggregationRecordNames = clusterConf.getAggregationRecordNames();
		assertEquals(1, aggregationRecordNames.size());
		assertEquals("featureAggregationRecord1", aggregationRecordNames.get(0));
		assertEquals(0.5, clusterConf.getWeight(), 0);

		clusterConf = clusterConfs.get(1);
		aggregationRecordNames = clusterConf.getAggregationRecordNames();
		assertEquals(1, aggregationRecordNames.size());
		assertEquals("featureAggregationRecord2", aggregationRecordNames.get(0));
		assertEquals(0.5, clusterConf.getWeight(), 0);

		clusterConf = clusterConfs.get(2);
		aggregationRecordNames = clusterConf.getAggregationRecordNames();
		assertEquals(1, aggregationRecordNames.size());
		assertEquals("scoreAggregationRecord1", aggregationRecordNames.get(0));
		assertEquals(0.5, clusterConf.getWeight(), 0);

		clusterConf = clusterConfs.get(3);
		aggregationRecordNames = clusterConf.getAggregationRecordNames();
		assertEquals(1, aggregationRecordNames.size());
		assertEquals("scoreAggregationRecord2", aggregationRecordNames.get(0));
		assertEquals(0.5, clusterConf.getWeight(), 0);
	}

	private void assertSecondSmartRecordConf(SmartRecordConf smartRecordConf) {
		assertEquals("test_smart_record_conf_2", smartRecordConf.getName());
		assertEquals(asList("userId", "srcMachineId"), smartRecordConf.getContexts());
		assertEquals(FixedDurationStrategy.DAILY, smartRecordConf.getFixedDurationStrategy());
		assertEquals(false, smartRecordConf.isIncludeAllAggregationRecords());
		assertEquals(0.25, smartRecordConf.getDefaultWeight(), 0);
		List<ClusterConf> clusterConfs = smartRecordConf.getClusterConfs().stream()
				.sorted(comparing(clusterConf -> clusterConf.getAggregationRecordNames().get(0)))
				.collect(Collectors.toList());
		assertEquals(2, clusterConfs.size());

		ClusterConf clusterConf = clusterConfs.get(0);
		List<String> aggregationRecordNames = clusterConf.getAggregationRecordNames();
		assertEquals(2, aggregationRecordNames.size());
		assertEquals("featureAggregationRecord1", aggregationRecordNames.get(0));
		assertEquals("scoreAggregationRecord1", aggregationRecordNames.get(1));
		assertEquals(0.25, clusterConf.getWeight(), 0);

		clusterConf = clusterConfs.get(1);
		aggregationRecordNames = clusterConf.getAggregationRecordNames();
		assertEquals(2, aggregationRecordNames.size());
		assertEquals("featureAggregationRecord2", aggregationRecordNames.get(0));
		assertEquals("scoreAggregationRecord2", aggregationRecordNames.get(1));
		assertEquals(0.75, clusterConf.getWeight(), 0);
	}

	@Configuration
	public static class SmartRecordConfServiceTestConfig {
		@Value("${base.configurations.path}")
		private String baseConfigurationsPath;

		@Bean
		public SmartRecordConfService smartRecordConfService() {
			AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService = mock(AggregatedFeatureEventsConfService.class);
			mockCompleteClusterConfs(aggregatedFeatureEventsConfService); // Relevant for the first smart record conf
			mockValidateSmartRecordConf(aggregatedFeatureEventsConfService); // Relevant for the second smart record conf
			return new SmartRecordConfService(baseConfigurationsPath, null, null, aggregatedFeatureEventsConfService);
		}

		@Bean
		public static TestPropertiesPlaceholderConfigurer smartRecordConfServiceTestPropertiesPlaceholderConfigurer() {
			Properties properties = new Properties();
			properties.put("base.configurations.path", "classpath:fortscale/config/asl/smart-records/*.json");
			return new TestPropertiesPlaceholderConfigurer(properties);
		}
	}

	private static void mockCompleteClusterConfs(AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService) {
		AggregatedFeatureEventConf featureAggregationRecordConf1 = mock(AggregatedFeatureEventConf.class);
		when(featureAggregationRecordConf1.getName()).thenReturn("featureAggregationRecord1");
		AggregatedFeatureEventConf featureAggregationRecordConf2 = mock(AggregatedFeatureEventConf.class);
		when(featureAggregationRecordConf2.getName()).thenReturn("featureAggregationRecord2");
		AggregatedFeatureEventConf scoreAggregationRecordConf1 = mock(AggregatedFeatureEventConf.class);
		when(scoreAggregationRecordConf1.getName()).thenReturn("scoreAggregationRecord1");
		AggregatedFeatureEventConf scoreAggregationRecordConf2 = mock(AggregatedFeatureEventConf.class);
		when(scoreAggregationRecordConf2.getName()).thenReturn("scoreAggregationRecord2");
		when(aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfs(eq(singletonList("userId")), eq(FixedDurationStrategy.HOURLY)))
				.thenReturn(asList(featureAggregationRecordConf1, featureAggregationRecordConf2, scoreAggregationRecordConf1, scoreAggregationRecordConf2));
	}

	private static void mockValidateSmartRecordConf(AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService) {
		FeatureBucketConf featureBucketConf1 = mock(FeatureBucketConf.class);
		when(featureBucketConf1.getContextFieldNames()).thenReturn(asList("userId", "srcMachineId"));
		when(featureBucketConf1.getStrategyName()).thenReturn("fixed_duration_daily");
		AggregatedFeatureEventConf featureAggregationRecordConf1 = mock(AggregatedFeatureEventConf.class);
		when(featureAggregationRecordConf1.getType()).thenReturn("F");
		when(featureAggregationRecordConf1.getBucketConf()).thenReturn(featureBucketConf1);
		when(aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf(eq("featureAggregationRecord1"))).thenReturn(featureAggregationRecordConf1);

		AggregatedFeatureEventConf scoreAggregationRecordConf1 = mock(AggregatedFeatureEventConf.class);
		when(scoreAggregationRecordConf1.getType()).thenReturn("P");
		when(scoreAggregationRecordConf1.getBucketConf()).thenReturn(featureBucketConf1);
		when(aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf(eq("scoreAggregationRecord1"))).thenReturn(scoreAggregationRecordConf1);

		FeatureBucketConf featureBucketConf2 = mock(FeatureBucketConf.class);
		when(featureBucketConf2.getContextFieldNames()).thenReturn(asList("userId", "srcMachineId"));
		when(featureBucketConf2.getStrategyName()).thenReturn("fixed_duration_daily");
		AggregatedFeatureEventConf featureAggregationRecordConf2 = mock(AggregatedFeatureEventConf.class);
		when(featureAggregationRecordConf2.getType()).thenReturn("F");
		when(featureAggregationRecordConf2.getBucketConf()).thenReturn(featureBucketConf2);
		when(aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf(eq("featureAggregationRecord2"))).thenReturn(featureAggregationRecordConf2);

		AggregatedFeatureEventConf scoreAggregationRecordConf2 = mock(AggregatedFeatureEventConf.class);
		when(scoreAggregationRecordConf2.getType()).thenReturn("P");
		when(scoreAggregationRecordConf2.getBucketConf()).thenReturn(featureBucketConf2);
		when(aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf(eq("scoreAggregationRecord2"))).thenReturn(scoreAggregationRecordConf2);
	}
}
