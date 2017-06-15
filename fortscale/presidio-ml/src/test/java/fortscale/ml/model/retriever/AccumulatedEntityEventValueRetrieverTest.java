package fortscale.ml.model.retriever;

import fortscale.accumulator.entityEvent.event.AccumulatedEntityEvent;
import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
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
	private AccumulatedEntityEventStore store;

	private AccumulatedEntityEvent createAccumulatedEntityEvent(String contextId, Double[] aggregatedFeatureScore) {
		Map<Integer,Double> aggregatedFeatureScoreMap = new HashMap<>();
		Set<Integer> activityTime = new HashSet<>();
		for (int i=0; i<aggregatedFeatureScore.length; i++){
			if(aggregatedFeatureScore[i] != null) {
				aggregatedFeatureScoreMap.put(i, aggregatedFeatureScore[i]);
				activityTime.add(i);
			}
		}
		return new AccumulatedEntityEvent(
				Instant.EPOCH,
				Instant.EPOCH,
				contextId,
				Collections.singletonMap(getFullAggregatedFeatureEventNameWithWeightOfOne(), aggregatedFeatureScoreMap),
				Instant.EPOCH,
				activityTime
		);
	}

	@Test
	public void shouldCreateStreamOfJokerEntityEventDataOutOfAccumulatedEntityEventDatas() {
		AccumulatedEntityEventValueRetrieverConf config = Mockito.mock(AccumulatedEntityEventValueRetrieverConf.class);
		EntityEventConf entityEventConf = registerEntityEventConf(config);
		AccumulatedEntityEventValueRetriever retriever = new AccumulatedEntityEventValueRetriever(config, entityEventConf);
		retriever.setStore(store);
		String contextId = "contextId";
		List<AccumulatedEntityEvent> accumulatedEntityEvents = Arrays.asList(
				createAccumulatedEntityEvent(contextId, new Double[]{0.1, 0.2, 0.2, 0.3}),
				createAccumulatedEntityEvent(contextId, new Double[]{0.4, 0.1})
		);
		when(store.findAccumulatedEventsByContextIdAndStartTimeRange(
				Mockito.eq(entityEventConf),
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
		retriever.setStore(store);
		String contextId = "contextId";
		Set<Integer> activityTime = new HashSet<>();
		activityTime.add(1);
		activityTime.add(2);

		HashMap<String, Map<Integer, Double>> aggregated_feature_events_values_map = new HashMap<>();
		HashMap<Integer, Double> activityMap = new HashMap<>();
		activityMap.put(2,3.0d);
		aggregated_feature_events_values_map.put("feature", activityMap);
		List<AccumulatedEntityEvent> accumulatedEntityEvents = Arrays.asList(
				new AccumulatedEntityEvent(Instant.EPOCH,Instant.EPOCH,contextId, aggregated_feature_events_values_map,Instant.EPOCH,activityTime)
		);
		when(store.findAccumulatedEventsByContextIdAndStartTimeRange(
				Mockito.eq(entityEventConf),
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
