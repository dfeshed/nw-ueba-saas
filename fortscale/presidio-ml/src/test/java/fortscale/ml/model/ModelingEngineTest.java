package fortscale.ml.model;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.IModelBuilderConf;
import fortscale.ml.model.metrics.ModelingServiceMetricsContainer;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.time.TimeRange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

public class ModelingEngineTest {
	private static final String DEFAULT_SESSION_ID = "testSessionId";

	private AbstractDataRetrieverConf retrieverConf;
	private ModelConf modelConf;
	private IContextSelector selector;
	private AbstractDataRetriever retriever;
	private IModelBuilder builder;
	private ModelStore store;
	private ModelingServiceMetricsContainer modelingServiceMetricsContainer;
	private IModelBuilderConf modelBuilderConf;

	@Before
	public void before() {
		retrieverConf = mock(AbstractDataRetrieverConf.class);
		modelConf = mock(ModelConf.class);
		retriever = mock(AbstractDataRetriever.class);
		builder = mock(IModelBuilder.class);
		store = mock(ModelStore.class);
		when(modelConf.getDataRetrieverConf()).thenReturn(retrieverConf);
		modelBuilderConf = mock(IModelBuilderConf.class);
		when(modelConf.getModelBuilderConf()).thenReturn(modelBuilderConf);
		when(modelConf.getModelBuilderConf().getFactoryName()).thenReturn("testFactoryName");
		when(modelConf.getName()).thenReturn("testName");
		modelingServiceMetricsContainer = mock(ModelingServiceMetricsContainer.class);
	}

	@Test
	public void should_select_retrieve_build_and_store_successfully_for_a_non_global_model() {
		when(retrieverConf.getTimeRangeInSeconds()).thenReturn(7776000L);
		selector = mock(IContextSelector.class); // Mocked selector for non global model
		Instant latestEndTime = Instant.ofEpochSecond(1489536000);
		List<String> contextIds = Arrays.asList("contextId1", "contextId2", "contextId3");
		TimeRange modelTimeRange = new TimeRange(1483228800, 1491004800); // 7776000 seconds
		List<Model> models = Arrays.asList(mock(Model.class), mock(Model.class), mock(Model.class));
		List<Boolean> successes = Arrays.asList(true, true, true);

		ModelingEngine modelingEngine = prepareMocks(latestEndTime, contextIds, modelTimeRange, models, successes);
		modelingEngine.process(DEFAULT_SESSION_ID, modelTimeRange.getEnd());

		verify(store).getLatestEndTime(eq(modelConf), eq(DEFAULT_SESSION_ID));
		verify(selector).getContexts(eq(new TimeRange(latestEndTime, modelTimeRange.getEnd())));
		Date endTime = Date.from(modelTimeRange.getEnd());

		for (int i = 0; i < contextIds.size(); i++) {
			String contextId = contextIds.get(i);
			Model model = models.get(i);
			verify(retriever).retrieve(eq(contextId), eq(endTime));
			verify(store).save(eq(modelConf), eq(DEFAULT_SESSION_ID), eq(contextId), eq(model), eq(modelTimeRange));
		}

		verify(builder, times(contextIds.size())).build(any());
		verifyNoMoreInteractions(selector, retriever, builder, store);
	}

	@Test
	public void should_select_retrieve_build_and_store_successfully_for_a_global_model() {
		when(retrieverConf.getTimeRangeInSeconds()).thenReturn(7776000L);
		selector = null; // No selector for global model
		List<String> contextIds = Collections.singletonList(null);
		TimeRange modelTimeRange = new TimeRange(1483228800, 1491004800); // 7776000 seconds
		List<Model> models = Collections.singletonList(mock(Model.class));
		List<Boolean> successes = Collections.singletonList(true);

		ModelingEngine modelingEngine = prepareMocks(null, contextIds, modelTimeRange, models, successes);
		modelingEngine.process(DEFAULT_SESSION_ID, modelTimeRange.getEnd());

		Date endTime = Date.from(modelTimeRange.getEnd());
		verify(retriever).retrieve(eq(null), eq(endTime));
		verify(builder).build(any());
		verify(store).save(eq(modelConf), eq(DEFAULT_SESSION_ID), eq(null), eq(models.get(0)), eq(modelTimeRange));
		verifyNoMoreInteractions(retriever, builder, store);
	}

	@Test
	public void should_finish_process_successfully_when_there_are_no_stored_models_with_the_given_session_id() {
		when(retrieverConf.getTimeRangeInSeconds()).thenReturn(7776000L);
		selector = mock(IContextSelector.class); // Mocked selector for non global model
		List<String> contextIds = Collections.singletonList("contextId1");
		TimeRange modelTimeRange = new TimeRange(1483228800, 1491004800); // 7776000 seconds
		List<Model> models = Collections.singletonList(mock(Model.class));
		List<Boolean> successes = Collections.singletonList(true);

		ModelingEngine modelingEngine = prepareMocks(null, contextIds, modelTimeRange, models, successes);
		modelingEngine.process(DEFAULT_SESSION_ID, modelTimeRange.getEnd());

		verify(store).getLatestEndTime(eq(modelConf), eq(DEFAULT_SESSION_ID));
		verify(selector).getContexts(eq(modelTimeRange));
		Date endTime = Date.from(modelTimeRange.getEnd());
		verify(retriever).retrieve(eq("contextId1"), eq(endTime));
		verify(builder).build(any());
		verify(store).save(eq(modelConf), eq(DEFAULT_SESSION_ID), eq("contextId1"), eq(models.get(0)), eq(modelTimeRange));
		verifyNoMoreInteractions(selector, retriever, builder, store);
	}

	@Test
	public void should_fail_storing_the_model_catch_exception_and_continue_as_usual() {
		when(retrieverConf.getTimeRangeInSeconds()).thenReturn(7776000L);
		selector = null; // No selector for global model
		List<String> contextIds = Collections.singletonList(null);
		TimeRange modelTimeRange = new TimeRange(1483228800, 1491004800); // 7776000 seconds
		List<Model> models = Collections.singletonList(mock(Model.class));
		List<Boolean> successes = Collections.singletonList(false);

		ModelingEngine modelingEngine = prepareMocks(null, contextIds, modelTimeRange, models, successes);
		modelingEngine.process(DEFAULT_SESSION_ID, modelTimeRange.getEnd());

		Date endTime = Date.from(modelTimeRange.getEnd());
		verify(retriever).retrieve(eq(null), eq(endTime));
		verify(builder).build(any());
		verify(store).save(eq(modelConf), eq(DEFAULT_SESSION_ID), eq(null), eq(models.get(0)), eq(modelTimeRange));
		verifyNoMoreInteractions(retriever, builder, store);
	}

	private ModelingEngine prepareMocks(
			Instant latestEndTime,
			List<String> contextIds,
			TimeRange modelTimeRange,
			List<Model> models,
			List<Boolean> successes) {

		if (selector != null) {
			when(store.getLatestEndTime(eq(modelConf), eq(DEFAULT_SESSION_ID))).thenReturn(latestEndTime);
			Set<String> setOfContextIds = contextIds.stream().collect(Collectors.toSet());
			when(selector.getContexts(any(TimeRange.class))).thenReturn(setOfContextIds);
		}

		Date endTime = Date.from(modelTimeRange.getEnd());

		for (int i = 0; i < contextIds.size(); i++) {
			String contextId = contextIds.get(i);
			Model model = models.get(i);
			Boolean success = successes.get(i);

			ModelBuilderData modelBuilderData = new ModelBuilderData(mock(Object.class));
			when(retriever.retrieve(eq(contextId), eq(endTime))).thenReturn(modelBuilderData);
			when(builder.build(eq(modelBuilderData.getData()))).thenReturn(model);

			if (!success) {
				doThrow(Exception.class)
						.when(store)
						.save(eq(modelConf), eq(DEFAULT_SESSION_ID), eq(contextId), eq(model), eq(modelTimeRange));
			}
		}

		return new ModelingEngine(modelConf, selector, retriever, builder, store, modelingServiceMetricsContainer);
	}
}
