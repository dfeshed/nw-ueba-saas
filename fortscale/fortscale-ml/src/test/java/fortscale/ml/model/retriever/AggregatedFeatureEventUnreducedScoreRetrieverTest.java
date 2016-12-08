package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.domain.core.FeatureScore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/retriever-test-context.xml"})
public class AggregatedFeatureEventUnreducedScoreRetrieverTest {
	@Autowired
	private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

	@Autowired
	private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

	@Test
	public void shouldFilterAggrEventsWithoutUnreducedScore() {
		AggregatedFeatureEventUnreducedScoreRetrieverConf config =
				Mockito.mock(AggregatedFeatureEventUnreducedScoreRetrieverConf.class);
		when(config.getScoreNameToCalibrate()).thenReturn("score to calibrate");
		AggregatedFeatureEventConf aggrFeatureEventConf = Mockito.mock(AggregatedFeatureEventConf.class);
		when(aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf(config.getAggregatedFeatureEventToCalibrateConfName()))
				.thenReturn(aggrFeatureEventConf);
		AggregatedFeatureEventUnreducedScoreRetriever retriever = new AggregatedFeatureEventUnreducedScoreRetriever(config);

		Map<Long, List<AggrEvent>> dateToTopAggFeatureEvents = new HashMap<>();
		Long yesterday = 1L;
		AggrEvent aggrEvent1 = Mockito.mock(AggrEvent.class);
		double unreducedScore = 76D;
		String scoreNameToCalibrate = config.getScoreNameToCalibrate();
		when(aggrEvent1.getFeatureScores()).thenReturn(createWrappedScore(scoreNameToCalibrate, unreducedScore));
		dateToTopAggFeatureEvents.put(yesterday, Collections.singletonList(aggrEvent1));
		Long twoDaysAgo = 2L;
		AggrEvent aggrEvent2 = Mockito.mock(AggrEvent.class);
		when(aggrEvent2.getFeatureScores()).thenReturn(createWrappedScore("wrong name", 42D));
		dateToTopAggFeatureEvents.put(twoDaysAgo, Collections.singletonList(aggrEvent2));

		Date endTime = new Date();
		when(aggregatedFeatureEventsReaderService.getDateToTopAggrEvents(
				aggrFeatureEventConf,
				endTime,
				config.getNumOfDays(),
				config.getNumOfIndicatorsPerDay())
		).thenReturn(dateToTopAggFeatureEvents);
		Map<Long, List<Double>> data = retriever.retrieve(null, endTime);
		Assert.assertEquals(new HashMap<Long, List<Double>>() {{
			put(yesterday, Collections.singletonList(unreducedScore));
			put(twoDaysAgo, Collections.emptyList());
		}}, data);
	}

	private List<FeatureScore> createWrappedScore(String scoreName, Double unreducedScore) {
		return Collections.singletonList(new FeatureScore(
				"wrapper",
				50D,
				Collections.singletonList(new FeatureScore(
						scoreName,
						unreducedScore
				))));
	}
}
