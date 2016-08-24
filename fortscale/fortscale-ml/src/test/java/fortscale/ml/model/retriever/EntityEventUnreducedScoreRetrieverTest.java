package fortscale.ml.model.retriever;

import fortscale.domain.core.EntityEvent;
import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.EntityEventMongoStore;
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
public class EntityEventUnreducedScoreRetrieverTest {
	@Autowired
	private EntityEventConfService entityEventConfService;

	@Autowired
	private EntityEventMongoStore entityEventMongoStore;

	@Test
	public void shouldFilterEntityEventsWithoutUnreducedScore() {
		EntityEventUnreducedScoreRetrieverConf config = Mockito.mock(EntityEventUnreducedScoreRetrieverConf.class);
		EntityEventConf entityEventConf = Mockito.mock(EntityEventConf.class);
		when(entityEventConfService.getEntityEventConf(config.getEntityEventConfName())).thenReturn(entityEventConf);
		EntityEventUnreducedScoreRetriever retriever = new EntityEventUnreducedScoreRetriever(config);

		Map<Long, List<EntityEvent>> dateToTopEntityEvents = new HashMap<>();
		Long yesterday = 1L;
		EntityEvent entityEvent1 = new EntityEvent();
		double unreducedScore = 76D;
		entityEvent1.setUnreduced_score(unreducedScore);
		dateToTopEntityEvents.put(yesterday, Collections.singletonList(entityEvent1));
		Long twoDaysAgo = 2L;
		EntityEvent entityEvent2 = new EntityEvent();
		entityEvent2.setUnreduced_score(null);
		dateToTopEntityEvents.put(twoDaysAgo, Collections.singletonList(entityEvent2));

		Date endTime = new Date();
		when(entityEventMongoStore.getDateToTopEntityEvents(
				config.getEntityEventConfName(),
				endTime,
				config.getNumOfDays(),
				config.getNumOfAlertsPerDay())
		).thenReturn(dateToTopEntityEvents);
		Map<Long, List<Double>> data = retriever.retrieve(null, endTime);
		Assert.assertEquals(new HashMap<Long, List<Double>>() {{
			put(yesterday, Collections.singletonList(unreducedScore));
			put(twoDaysAgo, Collections.emptyList());
		}}, data);
	}
}
