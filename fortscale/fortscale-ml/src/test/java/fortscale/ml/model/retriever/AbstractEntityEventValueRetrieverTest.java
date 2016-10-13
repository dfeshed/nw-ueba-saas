package fortscale.ml.model.retriever;

import fortscale.common.util.GenericHistogram;
import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.JokerEntityEventData;
import fortscale.ml.model.selector.EntityEventContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/retriever-test-context.xml"})
public class AbstractEntityEventValueRetrieverTest extends EntityEventValueRetrieverTestUtils {
	@Autowired
	private FactoryService<IContextSelector> contextSelectorFactoryService;

	protected JokerEntityEventData createJokerEntityEventData( double entityEventValue) {
		return new JokerEntityEventData(
				0,
				Collections.singletonMap(getFullAggregatedFeatureEventNameWithWeightOfOne(), entityEventValue)
		);
	}

	@Test
	public void shouldAggregateEntityEventValuesOfGivenContextIntoGenericHist() {
		AbstractEntityEventValueRetrieverConf config = Mockito.mock(AbstractEntityEventValueRetrieverConf.class);
		registerEntityEventConf(config);

		String contextIdToRetrieve = "contextId";
		AbstractEntityEventValueRetriever retriever = new AbstractEntityEventValueRetriever(config, false) {
			@Override
			protected Stream<JokerEntityEventData> readJokerEntityEventData(
					EntityEventConf entityEventConf,
					String contextId,
					Date startTime,
					Date endTime
			) {
				if (contextIdToRetrieve.equals(contextId)) {
					return Stream.of(
							createJokerEntityEventData(0.5),
							createJokerEntityEventData(0.5),
							createJokerEntityEventData(0.6)
					);
				}
				return null;
			}
		};
		GenericHistogram hist = (GenericHistogram) retriever.retrieve(contextIdToRetrieve, new Date());

		Assert.assertEquals(3, hist.getTotalCount(), 0.0000001);
		Assert.assertEquals(2, hist.get(0.5), 0.0000001);
		Assert.assertEquals(1, hist.get(0.6), 0.0000001);
	}

	@Test
	public void shouldAggregateEntityEventValuesOfAllContextsIntoGenericHist() {
		AbstractEntityEventValueRetrieverConf config = Mockito.mock(AbstractEntityEventValueRetrieverConf.class);
		registerEntityEventConf(config);

		String contextId1 = "contextId1";
		String contextId2 = "contextId2";
		String contextId3 = "contextId3";
		when(contextSelectorFactoryService.getProduct(Mockito.any(EntityEventContextSelectorConf.class)))
				.thenReturn((startTime, endTime) -> Arrays.asList(contextId1, contextId2, contextId3));

		AbstractEntityEventValueRetriever retriever = new AbstractEntityEventValueRetriever(config, false) {
			@Override
			protected Stream<JokerEntityEventData> readJokerEntityEventData(
					EntityEventConf entityEventConf,
					String contextId,
					Date startTime,
					Date endTime
			) {
				if (contextId1.equals(contextId)) {
					return Stream.of(
							createJokerEntityEventData(0.5),
							createJokerEntityEventData(0.5),
							createJokerEntityEventData(0.6)
					);
				} else if (contextId2.equals(contextId)) {
					return Stream.of(
							createJokerEntityEventData(0.5),
							createJokerEntityEventData(0.7)
					);
				} else if (contextId3.equals(contextId)) {
					return Stream.of(
							createJokerEntityEventData(0.1),
							createJokerEntityEventData(0.7)
					);
				}
				return null;
			}
		};
		GenericHistogram hist = (GenericHistogram) retriever.retrieve(null, new Date());

		Assert.assertEquals(3, hist.getTotalCount(), 0.0000001);
		Assert.assertEquals(1, hist.get(0.6), 0.0000001);
		Assert.assertEquals(2, hist.get(0.7), 0.0000001);
	}
}
