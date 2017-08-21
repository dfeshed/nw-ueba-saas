package fortscale.ml.model.retriever;

import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.JokerAggrEventData;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/retriever-test-context.xml"})
@SpringBootTest
public class AccumulatedEntityEventValueRetrieverTest extends EntityEventValueRetrieverTestUtils {

	@MockBean
	FactoryService contextSelectorFactoryService;

	@MockBean
	private SmartAccumulationDataReader reader;

	private AccumulatedSmartRecord createAccumulatedEntityEvent(String contextId, Double[] aggregatedFeatureScore) {
		Map<Integer,Double> aggregatedFeatureScoreMap = new HashMap<>();
		Set<Integer> activityTime = new HashSet<>();
		for (int i=0; i<aggregatedFeatureScore.length; i++){
			if(aggregatedFeatureScore[i] != null) {
				aggregatedFeatureScoreMap.put(i, aggregatedFeatureScore[i]);
				activityTime.add(i);
			}
		}

		AccumulatedSmartRecord accumulatedSmartRecord = new AccumulatedSmartRecord(
				Instant.EPOCH,
				Instant.EPOCH,
				contextId,
				"featureName"
		);

		accumulatedSmartRecord.setAggregatedFeatureEventsValuesMap(Collections.singletonMap(getFullAggregatedFeatureEventNameWithWeightOfOne(), aggregatedFeatureScoreMap));
		accumulatedSmartRecord.setActivityTime(activityTime);
			return accumulatedSmartRecord;
	}

	@Test
	public void shouldCreateStreamOfJokerEntityEventDataOutOfAccumulatedEntityEventDatas() {
		AccumulatedEntityEventValueRetrieverConf config = Mockito.mock(AccumulatedEntityEventValueRetrieverConf.class);
		EntityEventConf entityEventConf = registerEntityEventConf(config);
		AccumulatedEntityEventValueRetriever retriever = new AccumulatedEntityEventValueRetriever(config, entityEventConf);
		retriever.setReader(reader);
		String contextId = "contextId";
		List<AccumulatedSmartRecord> accumulatedEntityEvents = Arrays.asList(
				createAccumulatedEntityEvent(contextId, new Double[]{0.1, 0.2, 0.2, 0.3}),
				createAccumulatedEntityEvent(contextId, new Double[]{0.4, 0.1})
		);
		when(reader.findAccumulatedEventsByContextIdAndStartTimeRange(
				Mockito.eq(entityEventConf.getName()),
				Mockito.eq(contextId),
				Mockito.any(Instant.class),
				Mockito.any(Instant.class)
		)).thenReturn(accumulatedEntityEvents);

		List<Double> scores = retriever.readJokerEntityEventData(
				entityEventConf,
				contextId,
				new Date(),
				new Date()
		)
				.flatMapToDouble(jokerEntityEventData -> jokerEntityEventData.getJokerAggrEventDatas().stream().mapToDouble(JokerAggrEventData::getScore))
				.boxed()
				.sorted()
				.collect(Collectors.toList());

		Assert.assertEquals(Arrays.asList(0.1, 0.1, 0.2, 0.2, 0.3, 0.4), scores);
	}

	@Test
	public void  shouldCreateStreamOfJokerEntityEventDataOutOfAccumulatedEntityEventDatasWithZeroScore() {
		AccumulatedEntityEventValueRetrieverConf config = Mockito.mock(AccumulatedEntityEventValueRetrieverConf.class);
		EntityEventConf entityEventConf = registerEntityEventConf(config);
		AccumulatedEntityEventValueRetriever retriever = new AccumulatedEntityEventValueRetriever(config, entityEventConf);
		retriever.setReader(reader);
		String contextId = "contextId";
		Set<Integer> activityTime = new HashSet<>();
		activityTime.add(1);
		activityTime.add(2);

		HashMap<String, Map<Integer, Double>> aggregated_feature_events_values_map = new HashMap<>();
		HashMap<Integer, Double> activityMap = new HashMap<>();
		activityMap.put(2,3.0d);
		aggregated_feature_events_values_map.put("feature", activityMap);


		AccumulatedSmartRecord accumulatedSmartRecord = new AccumulatedSmartRecord(Instant.EPOCH,Instant.EPOCH,contextId,"featureName");
		accumulatedSmartRecord.setAggregatedFeatureEventsValuesMap(aggregated_feature_events_values_map);
		accumulatedSmartRecord.setActivityTime(activityTime);


		List<AccumulatedSmartRecord> accumulatedEntityEvents = Arrays.asList(accumulatedSmartRecord);
		when(reader.findAccumulatedEventsByContextIdAndStartTimeRange(
				Mockito.eq(entityEventConf.getName()),
				Mockito.eq(contextId),
				Mockito.any(Instant.class),
				Mockito.any(Instant.class)
		)).thenReturn(accumulatedEntityEvents);

		List<Double> scores = retriever.readJokerEntityEventData(
				entityEventConf,
				contextId,
				new Date(),
				new Date()
		)
				.flatMapToDouble(jokerEntityEventData -> jokerEntityEventData.getJokerAggrEventDatas().stream().mapToDouble(JokerAggrEventData::getScore))
				.boxed()
				.sorted()
				.collect(Collectors.toList());

		Assert.assertEquals(Arrays.asList(3.0), scores);
	}
}
