package fortscale.ml.model.retriever;

import fortscale.accumulator.entityEvent.event.AccumulatedEntityEvent;
import fortscale.accumulator.entityEvent.store.AccumulatedEntityEventStore;
import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.JokerAggrEventData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/retriever-test-context.xml"})
public class AccumulatedEntityEventValueRetrieverTest extends EntityEventValueRetrieverTestUtils {
	@Autowired
	private AccumulatedEntityEventStore store;

	private AccumulatedEntityEvent createAccumulatedEntityEvent(String contextId, Double[] aggregatedFeatureScore) {
		return new AccumulatedEntityEvent(
				Instant.EPOCH,
				Instant.EPOCH,
				contextId,
				Collections.singletonMap(getFullAggregatedFeatureEventNameWithWeightOfOne(), aggregatedFeatureScore),
				Instant.EPOCH
		);
	}

	@Test
	public void shouldCreateStreamOfJokerEntityEventDataOutOfAccumulatedEntityEventDatas() {
		AccumulatedEntityEventValueRetrieverConf config = Mockito.mock(AccumulatedEntityEventValueRetrieverConf.class);
		EntityEventConf entityEventConf = registerEntityEventConf(config);
		AccumulatedEntityEventValueRetriever retriever = new AccumulatedEntityEventValueRetriever(config);
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
}
