package fortscale.streaming.service.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.ModelService;
import fortscale.ml.model.listener.IModelBuildingListener;
import net.minidev.json.JSONObject;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ModelBuildingRegistrationServiceTest {
	private static final String KEY_DELIMITER = "#";

	private static ClassPathXmlApplicationContext testContextManager;

	private ModelConfService modelConfService;
	private ModelService modelService;
	private IModelBuildingListener modelBuildingListener;
	private ModelBuildingSamzaStore modelBuildingStore;
	private ModelBuildingRegistrationService regService;

	@BeforeClass
	public static void setUpClass() {
		testContextManager = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/model-building-registration-service-test-context.xml");
	}

	@Before
	public void setUp() {
		modelConfService = testContextManager.getBean(ModelConfService.class);
		modelService = testContextManager.getBean(ModelService.class);
		modelBuildingListener = testContextManager.getBean(IModelBuildingListener.class);
		modelBuildingStore = testContextManager.getBean(ModelBuildingSamzaStore.class);
		regService = new ModelBuildingRegistrationService(modelBuildingListener, modelBuildingStore,null);
		reset(modelConfService, modelService, modelBuildingListener, modelBuildingStore);
	}

	@Test
	public void registration_service_should_filter_all_models_by_not_regex() throws JsonProcessingException {
		regService =  new ModelBuildingRegistrationService(modelBuildingListener, modelBuildingStore,"^(?!entity).*");
		// For one session, register all models
		String sessionId = "mySession";
		String modelConfName = "all_models";
		long endTimeInSeconds = 3000;

		// Two models exist
		String modelConfName1 = "modelConf1";
		ModelConf modelConf1 = mock(ModelConf.class);
		String modelConfName2 = "entity_modelConf2";
		ModelConf modelConf2 = mock(ModelConf.class);
		List<ModelConf> modelConfs = Arrays.asList(modelConf1, modelConf2);
		when(modelConfService.getModelConfs()).thenReturn(modelConfs);
		when(modelConf1.getName()).thenReturn(modelConfName1);
		when(modelConf2.getName()).thenReturn(modelConfName2);

		// Second model is already registered
		Date previousEndTime = new Date();
		Date currentEndTime = new Date();
		boolean selectHighScoreContexts = false;
		ModelBuildingRegistration existingRegistration = new ModelBuildingRegistration(sessionId, modelConfName2, previousEndTime, currentEndTime, selectHighScoreContexts);
		when(modelBuildingStore.getRegistration(eq(sessionId), eq(modelConfName2), eq(selectHighScoreContexts))).thenReturn(existingRegistration);

		// Act
		regService.process(createEvent(sessionId, modelConfName, endTimeInSeconds, selectHighScoreContexts));
		verify(modelBuildingStore,times(1)).storeRegistration(any(ModelBuildingRegistration.class));

	}

	@Test
	public void registration_service_should_filter_all_models_by_regex() throws JsonProcessingException {
		regService =  new ModelBuildingRegistrationService(modelBuildingListener, modelBuildingStore,"^entity_.*");
		// For one session, register all models
		String sessionId = "mySession";
		String modelConfName = "all_models";
		long endTimeInSeconds = 3000;

		// Two models exist
		String modelConfName1 = "modelConf1";
		ModelConf modelConf1 = mock(ModelConf.class);
		String modelConfName2 = "entity_modelConf2";
		ModelConf modelConf2 = mock(ModelConf.class);
		List<ModelConf> modelConfs = Arrays.asList(modelConf1, modelConf2);
		when(modelConfService.getModelConfs()).thenReturn(modelConfs);
		when(modelConf1.getName()).thenReturn(modelConfName1);
		when(modelConf2.getName()).thenReturn(modelConfName2);

		// Second model is already registered
		Date previousEndTime = new Date();
		Date currentEndTime = new Date();
		boolean selectHighScoreContexts = false;
		ModelBuildingRegistration existingRegistration = new ModelBuildingRegistration(sessionId, modelConfName2, previousEndTime, currentEndTime, selectHighScoreContexts);
		when(modelBuildingStore.getRegistration(eq(sessionId), eq(modelConfName2), eq(selectHighScoreContexts))).thenReturn(existingRegistration);

		// Act
		regService.process(createEvent(sessionId, modelConfName, endTimeInSeconds, selectHighScoreContexts));
		verify(modelBuildingStore,times(1)).storeRegistration(any(ModelBuildingRegistration.class));

	}

	@Test
	public void registration_service_should_process_an_event_for_one_session_and_one_model_conf_correctly() throws JsonProcessingException {
		// New registration
		String sessionId1 = "mySession1";
		String modelConfName1 = "myModelConf1";
		long endTimeInSeconds1 = 1000;

		// Existing registration
		String sessionId2 = "mySession2";
		String modelConfName2 = "myModelConf2";
		Date previousEndTime2 = new Date();
		Date currentEndTime2 = new Date();
		long endTimeInSeconds2 = 2000;
		Boolean selectHighScoreContexts = false;
		ModelBuildingRegistration existingRegistration = new ModelBuildingRegistration(sessionId2, modelConfName2, previousEndTime2, currentEndTime2, selectHighScoreContexts);
		when(modelBuildingStore.getRegistration(eq(sessionId2), eq(modelConfName2), eq(selectHighScoreContexts))).thenReturn(existingRegistration);

		// Act
		regService.process(createEvent(sessionId1, modelConfName1, endTimeInSeconds1, selectHighScoreContexts));
		regService.process(createEvent(sessionId2, modelConfName2, endTimeInSeconds2, selectHighScoreContexts));

		// Captor arguments
		ArgumentCaptor<ModelBuildingRegistration> argumentCaptor = ArgumentCaptor.forClass(ModelBuildingRegistration.class);
		verify(modelBuildingStore, times(2)).storeRegistration(argumentCaptor.capture());

		// Assert new registration
		ModelBuildingRegistration actualRegistration = argumentCaptor.getAllValues().get(0);
		Assert.assertEquals(sessionId1, actualRegistration.getSessionId());
		Assert.assertEquals(modelConfName1, actualRegistration.getModelConfName());
		Assert.assertEquals(null, actualRegistration.getPreviousEndTime());
		Assert.assertEquals(new Date(TimeUnit.SECONDS.toMillis(endTimeInSeconds1)), actualRegistration.getCurrentEndTime());
		Assert.assertEquals(selectHighScoreContexts, actualRegistration.selectHighScoreContexts());

		// Assert existing registration
		actualRegistration = argumentCaptor.getAllValues().get(1);
		Assert.assertEquals(sessionId2, actualRegistration.getSessionId());
		Assert.assertEquals(modelConfName2, actualRegistration.getModelConfName());
		Assert.assertEquals(previousEndTime2, actualRegistration.getPreviousEndTime());
		Assert.assertEquals(new Date(TimeUnit.SECONDS.toMillis(endTimeInSeconds2)), actualRegistration.getCurrentEndTime());
		Assert.assertEquals(selectHighScoreContexts, actualRegistration.selectHighScoreContexts());
	}

	@Test
	public void registration_service_should_process_an_event_for_one_session_and_all_model_confs_correctly() throws JsonProcessingException {
		// For one session, register all models
		String sessionId = "mySession";
		String modelConfName = "all_models";
		long endTimeInSeconds = 3000;

		// Two models exist
		String modelConfName1 = "modelConf1";
		ModelConf modelConf1 = mock(ModelConf.class);
		String modelConfName2 = "modelConf2";
		ModelConf modelConf2 = mock(ModelConf.class);
		List<ModelConf> modelConfs = Arrays.asList(modelConf1, modelConf2);
		when(modelConfService.getModelConfs()).thenReturn(modelConfs);
		when(modelConf1.getName()).thenReturn(modelConfName1);
		when(modelConf2.getName()).thenReturn(modelConfName2);

		// Second model is already registered
		Date previousEndTime = new Date();
		Date currentEndTime = new Date();
		boolean selectHighScoreContexts = false;
		ModelBuildingRegistration existingRegistration = new ModelBuildingRegistration(sessionId, modelConfName2, previousEndTime, currentEndTime, selectHighScoreContexts);
		when(modelBuildingStore.getRegistration(eq(sessionId), eq(modelConfName2), eq(selectHighScoreContexts))).thenReturn(existingRegistration);

		// Act
		regService.process(createEvent(sessionId, modelConfName, endTimeInSeconds, selectHighScoreContexts));

		// Captor arguments
		ArgumentCaptor<ModelBuildingRegistration> argumentCaptor = ArgumentCaptor.forClass(ModelBuildingRegistration.class);
		verify(modelBuildingStore, times(2)).storeRegistration(argumentCaptor.capture());

		// Assert new registration
		ModelBuildingRegistration actualRegistration = argumentCaptor.getAllValues().get(0);
		Assert.assertEquals(sessionId, actualRegistration.getSessionId());
		Assert.assertEquals(modelConfName1, actualRegistration.getModelConfName());
		Assert.assertEquals(null, actualRegistration.getPreviousEndTime());
		Date expectedCurrentEndTime = new Date(TimeUnit.SECONDS.toMillis(endTimeInSeconds));
		Assert.assertEquals(expectedCurrentEndTime, actualRegistration.getCurrentEndTime());
		Assert.assertEquals(selectHighScoreContexts, actualRegistration.selectHighScoreContexts());

		// Assert existing registration
		actualRegistration = argumentCaptor.getAllValues().get(1);
		Assert.assertEquals(sessionId, actualRegistration.getSessionId());
		Assert.assertEquals(modelConfName2, actualRegistration.getModelConfName());
		Assert.assertEquals(previousEndTime, actualRegistration.getPreviousEndTime());
		Assert.assertEquals(expectedCurrentEndTime, actualRegistration.getCurrentEndTime());
		Assert.assertEquals(selectHighScoreContexts, actualRegistration.selectHighScoreContexts());
	}

	@Test
	public void registration_service_should_ignore_event_with_invalid_session_id() throws JsonProcessingException {
		regService.process(createEvent("", "myModelConf", 4000L, false));
		verifyNoMoreInteractions(modelConfService);
		verifyNoMoreInteractions(modelBuildingStore);
	}

	@Test
	public void registration_service_should_ignore_event_with_invalid_model_conf_name() throws JsonProcessingException {
		regService.process(createEvent("mySession", "   ", 5000L, false));
		verifyNoMoreInteractions(modelConfService);
		verifyNoMoreInteractions(modelBuildingStore);
	}

	@Test
	public void registration_service_should_ignore_event_with_invalid_end_time() throws JsonProcessingException {
		regService.process(createEvent("mySession", "myModelConf", null, false));
		verifyNoMoreInteractions(modelConfService);
		verifyNoMoreInteractions(modelBuildingStore);
	}

	@Test
	public void registration_service_should_delete_one_registration() throws JsonProcessingException {
		// Arrange
		String sessionId = "mySession";
		String modelConfName = "myModelConf";

		// Act
		boolean selectHighScoreContexts = false;
		regService.process(createEvent(sessionId, modelConfName, -1L, selectHighScoreContexts));

		// Assert
		verify(modelBuildingStore).deleteRegistration(eq(sessionId), eq(modelConfName), eq(selectHighScoreContexts));
		verifyNoMoreInteractions(modelConfService);
		verifyNoMoreInteractions(modelBuildingStore);
	}

	@Test
	public void registration_service_should_delete_all_registrations_for_one_session() throws JsonProcessingException {
		// Arrange
		String sessionId = "mySession";
		String modelConfName = "ALL_MODELS";

		ModelConf modelConf1 = mock(ModelConf.class);
		ModelConf modelConf2 = mock(ModelConf.class);
		String modelConfName1 = "myModelConf1";
		String modelConfName2 = "myModelConf2";
		when(modelConf1.getName()).thenReturn(modelConfName1);
		when(modelConf2.getName()).thenReturn(modelConfName2);
		when(modelConfService.getModelConfs()).thenReturn(Arrays.asList(modelConf1, modelConf2));

		// Act
		boolean selectHighScoreContexts = false;
		regService.process(createEvent(sessionId, modelConfName, -100L, selectHighScoreContexts));

		// Assert
		verify(modelConfService).getModelConfs();
		verify(modelBuildingStore).deleteRegistration(eq(sessionId), eq(modelConfName1), eq(selectHighScoreContexts));
		verify(modelBuildingStore).deleteRegistration(eq(sessionId), eq(modelConfName2), eq(selectHighScoreContexts));
		verifyNoMoreInteractions(modelConfService);
		verifyNoMoreInteractions(modelBuildingStore);
	}

	@Test
	public void registration_service_should_execute_all_registrations_and_update_their_end_times() {
		// Arrange
		final String sessionId1 = "session1";
		final String sessionId2 = "session2";
		final String modelConfName1 = "modelConf1";
		final String modelConfName2 = "modelConf2";
		final Date previousEndTime1 = new Date(1000);
		final Date previousEndTime2 = new Date(2000);
		final Date currentEndTime1 = new Date(3000);
		final Date currentEndTime2 = new Date(4000);

		Boolean selectHighScoreContexts = false;
		final ModelBuildingRegistration reg1 = new ModelBuildingRegistration(sessionId1, modelConfName1, previousEndTime1, currentEndTime1, selectHighScoreContexts);
		when(modelBuildingStore.getRegistration(eq(getKey(sessionId1, modelConfName1, selectHighScoreContexts)))).thenReturn(reg1);
		final ModelBuildingRegistration reg2 = new ModelBuildingRegistration(sessionId2, modelConfName1, previousEndTime2, currentEndTime2, selectHighScoreContexts);
		when(modelBuildingStore.getRegistration(eq(getKey(sessionId2, modelConfName1, selectHighScoreContexts)))).thenReturn(reg2);
		final ModelBuildingRegistration reg3 = new ModelBuildingRegistration(sessionId2, modelConfName2, previousEndTime2, currentEndTime2, selectHighScoreContexts);
		when(modelBuildingStore.getRegistration(eq(getKey(sessionId2, modelConfName2, selectHighScoreContexts)))).thenReturn(reg3);

		// Imitate iterator
		when(modelBuildingStore.getIterator()).thenReturn(new KeyValueIterator<String, ModelBuildingRegistration>() {
			@Override
			public void close() {}

			private int counter = 3;

			@Override
			public boolean hasNext() {
				return counter > 0;
			}

			@Override
			public Entry<String, ModelBuildingRegistration> next() {
				switch (counter--) {
					case 3: return new Entry<>(getKey(reg1.getSessionId(), reg1.getModelConfName(), selectHighScoreContexts), reg1);
					case 2: return new Entry<>(getKey(reg2.getSessionId(), reg2.getModelConfName(), selectHighScoreContexts), reg2);
					case 1: return new Entry<>(getKey(reg3.getSessionId(), reg3.getModelConfName(), selectHighScoreContexts), reg3);
					default: return null;
				}
			}

			@Override
			public void remove() {}
		});

		// Act
		regService.window();

		// Verify interactions
		verify(modelBuildingStore).getIterator();
		verify(modelService).process(eq(modelBuildingListener), eq(sessionId1), eq(modelConfName1),
				eq(previousEndTime1), eq(currentEndTime1), eq(selectHighScoreContexts));
		verify(modelService).process(eq(modelBuildingListener), eq(sessionId2), eq(modelConfName1),
				eq(previousEndTime2), eq(currentEndTime2), eq(selectHighScoreContexts));
		verify(modelService).process(eq(modelBuildingListener), eq(sessionId2), eq(modelConfName2),
				eq(previousEndTime2), eq(currentEndTime2), eq(selectHighScoreContexts));
		verify(modelBuildingStore).storeRegistration(eq(reg1));
		verify(modelBuildingStore).storeRegistration(eq(reg2));
		verify(modelBuildingStore).storeRegistration(eq(reg3));
		verifyNoMoreInteractions(modelService);
		verifyNoMoreInteractions(modelBuildingListener);

		// Assert registration 1
		Assert.assertEquals(sessionId1, reg1.getSessionId());
		Assert.assertEquals(modelConfName1, reg1.getModelConfName());
		Assert.assertEquals(currentEndTime1, reg1.getPreviousEndTime());
		Assert.assertEquals(null, reg1.getCurrentEndTime());
		Assert.assertEquals(selectHighScoreContexts, reg1.selectHighScoreContexts());

		// Assert registration 2
		Assert.assertEquals(sessionId2, reg2.getSessionId());
		Assert.assertEquals(modelConfName1, reg2.getModelConfName());
		Assert.assertEquals(currentEndTime2, reg2.getPreviousEndTime());
		Assert.assertEquals(null, reg2.getCurrentEndTime());
		Assert.assertEquals(selectHighScoreContexts, reg2.selectHighScoreContexts());

		// Assert registration 3
		Assert.assertEquals(sessionId2, reg3.getSessionId());
		Assert.assertEquals(modelConfName2, reg3.getModelConfName());
		Assert.assertEquals(currentEndTime2, reg3.getPreviousEndTime());
		Assert.assertEquals(null, reg3.getCurrentEndTime());
		Assert.assertEquals(selectHighScoreContexts, reg3.selectHighScoreContexts());
	}

	private static JSONObject createEvent(String sessionId, String modelConfName, Long endTimeInSeconds, boolean selectHighScoreContexts) throws JsonProcessingException {
		JSONObject event = new JSONObject();
		event.put("sessionId", sessionId);
		event.put("modelConfName", modelConfName);
		event.put("endTimeInSeconds", endTimeInSeconds);
		event.put("selectHighScoreContexts", selectHighScoreContexts);
		return event;
	}

	private static String getKey(String sessionId, String modelConfName, boolean selectHighScoreContexts) {
		return String.format("%s%s%s%b", sessionId, KEY_DELIMITER, modelConfName, selectHighScoreContexts);
	}
}
