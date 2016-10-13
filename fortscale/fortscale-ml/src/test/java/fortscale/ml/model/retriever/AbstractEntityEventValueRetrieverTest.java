package fortscale.ml.model.retriever;

import fortscale.common.util.GenericHistogram;
import fortscale.entity.event.EntityEventConf;
import fortscale.entity.event.EntityEventConfService;
import fortscale.entity.event.JokerEntityEventData;
import fortscale.ml.model.selector.EntityEventContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.FactoryService;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/retriever-test-context.xml"})
public class AbstractEntityEventValueRetrieverTest {
	@Autowired
	private EntityEventConfService entityEventConfService;
	@Autowired
	private FactoryService<IContextSelector> contextSelectorFactoryService;

	private static final String FULL_AGGREGATED_FEATURE_EVENT_NAME = "featureBucket.featureName";

	private void registerEntityEventConf(AbstractEntityEventValueRetrieverConf config) {
		String entityEventConfName = "entityEventConfName";
		EntityEventConf entityEventConf = Mockito.mock(EntityEventConf.class);
		when(entityEventConf.getName()).thenReturn(entityEventConfName);
		when(config.getEntityEventConfName()).thenReturn(entityEventConfName);
		when(entityEventConfService.getEntityEventConf(config.getEntityEventConfName())).thenReturn(entityEventConf);

		Map<String, Object> entityEventFunction = new HashMap<>();
		entityEventFunction.put("clusters", Collections.emptyMap());
		entityEventFunction.put("alphas", Collections.emptyMap());
		entityEventFunction.put("betas", Collections.singletonMap(FULL_AGGREGATED_FEATURE_EVENT_NAME, 1.0));
		when(entityEventConf.getEntityEventFunction()).thenReturn(new JSONObject(entityEventFunction));
	}

	private JokerEntityEventData createJokerEntityEventData( double entityEventValue) {
		return new JokerEntityEventData(0, Collections.singletonMap(FULL_AGGREGATED_FEATURE_EVENT_NAME, entityEventValue));
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
		when(contextSelectorFactoryService.getProduct(Mockito.any(EntityEventContextSelectorConf.class)))
				.thenReturn((startTime, endTime) -> Arrays.asList(contextId1, contextId2));

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
				}
				return null;
			}
		};
		GenericHistogram hist = (GenericHistogram) retriever.retrieve(null, new Date());

		Assert.assertEquals(5, hist.getTotalCount(), 0.0000001);
		Assert.assertEquals(3, hist.get(0.5), 0.0000001);
		Assert.assertEquals(1, hist.get(0.6), 0.0000001);
		Assert.assertEquals(1, hist.get(0.7), 0.0000001);
	}
}
