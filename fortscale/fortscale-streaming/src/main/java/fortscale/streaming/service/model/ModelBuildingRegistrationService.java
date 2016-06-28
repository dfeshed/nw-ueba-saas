package fortscale.streaming.service.model;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.ModelService;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.streaming.service.model.metrics.ModelBuildingRegistrationServiceMetrics;
import fortscale.streaming.service.model.metrics.ModelBuildingRegistrationServiceSetMetrics;
import fortscale.utils.ConversionUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import net.minidev.json.JSONObject;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

import static fortscale.utils.time.TimestampUtils.convertToMilliSeconds;
import static fortscale.utils.time.TimestampUtils.convertToSeconds;

@Configurable(preConstruction = true)
public class ModelBuildingRegistrationService {
	private static final Logger logger = Logger.getLogger(ModelBuildingRegistrationService.class);

	@Autowired
	private ModelConfService modelConfService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private StatsService statsService;

	@Value("${fortscale.model.build.message.field.session.id}")
	private String sessionIdJsonField;
	@Value("${fortscale.model.build.message.field.model.conf.name}")
	private String modelConfNameJsonField;
	@Value("${fortscale.model.build.message.field.end.time.in.seconds}")
	private String endTimeInSecondsJsonField;
	@Value("${fortscale.model.build.message.constant.all.models}")
	private String allModelsConstantValue;

	private IModelBuildingListener modelBuildingListener;
	private ModelBuildingSamzaStore modelBuildingStore;
	private ModelBuildingRegistrationServiceMetrics metrics;
	private Map<String, ModelBuildingRegistrationServiceSetMetrics> setNameToMetrics;

	public ModelBuildingRegistrationService(
			IModelBuildingListener modelBuildingListener,
			ModelBuildingSamzaStore modelBuildingStore) {

		// modelBuildingListener can be null, if there is no listener
		Assert.notNull(modelBuildingStore);
		this.modelBuildingListener = modelBuildingListener;
		this.modelBuildingStore = modelBuildingStore;
		this.metrics = new ModelBuildingRegistrationServiceMetrics(statsService);
		this.setNameToMetrics = new HashMap<>();

		modelService.init();
	}

	public void process(JSONObject event) {
		String sessionId = event.getAsString(sessionIdJsonField);
		String modelConfName = event.getAsString(modelConfNameJsonField);
		Long endTimeSec = ConversionUtils.convertToLong(event.get(endTimeInSecondsJsonField));

		if (StringUtils.hasText(sessionId) && StringUtils.hasText(modelConfName) && endTimeSec != null) {
			metrics.processed++;
			getSetMetrics(modelConfName).processed++; // TODO: modelConfName can be "ALL_MODELS" and later on "RAW_EVENT_MODELS" for example
			Date endTime = endTimeSec < 0 ? null : new Date(convertToMilliSeconds(endTimeSec));

			if (modelConfName.equalsIgnoreCase(allModelsConstantValue)) {
				for (ModelConf modelConf : modelConfService.getModelConfs()) {
					process(sessionId, modelConf.getName(), endTime);
				}
			} else {
				process(sessionId, modelConfName, endTime);
			}
		} else {
			metrics.ignored++;
			logger.error("Ignoring message with invalid arguments: {}.", event.toJSONString());
		}
	}

	public void window() {
		KeyValueIterator<String, ModelBuildingRegistration> iterator = null;
		List<ModelBuildingRegistration> registrationsToUpdate = new ArrayList<>();

		try {
			iterator = modelBuildingStore.getIterator();

			while (iterator.hasNext()) {
				String key = iterator.next().getKey();
				ModelBuildingRegistration reg = modelBuildingStore.getRegistration(key);

				if (reg == null) {
					metrics.nullRegistrations++;
				} else if (reg.getCurrentEndTime() == null) {
					metrics.pendingRegistrations++;
					getSetMetrics(reg.getModelConfName()).pendingRegistrations++;
				} else {
					modelService.process(modelBuildingListener, reg.getSessionId(), reg.getModelConfName(),
							reg.getPreviousEndTime(), reg.getCurrentEndTime());
					metrics.handledRegistrations++;
					getSetMetrics(reg.getModelConfName()).handledRegistrations++;
					getSetMetrics(reg.getModelConfName()).lastHandledEndTime = convertToSeconds(reg.getCurrentEndTime());
					reg.setPreviousEndTime(reg.getCurrentEndTime());
					reg.setCurrentEndTime(null);
					registrationsToUpdate.add(reg);
				}
			}
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}

		for (ModelBuildingRegistration reg : registrationsToUpdate) {
			modelBuildingStore.storeRegistration(reg);
		}
	}

	private void process(String sessionId, String modelConfName, Date endTime) {
		if (endTime == null) {
			modelBuildingStore.deleteRegistration(sessionId, modelConfName);
			metrics.delete++;
			getSetMetrics(modelConfName).delete++;
			return;
		}

		ModelBuildingRegistration registration = modelBuildingStore.getRegistration(sessionId, modelConfName);

		if (registration == null) {
			registration = new ModelBuildingRegistration(sessionId, modelConfName, null, endTime);
		} else {
			Date currentEndTime = registration.getCurrentEndTime();

			if (currentEndTime != null && currentEndTime.after(endTime)) {
				metrics.storeWithEarlierEndTime++;
				getSetMetrics(modelConfName).storeWithEarlierEndTime++;
				logger.warn("Overwriting later end time {} for session {} and model conf {} with earlier end time {}.",
						currentEndTime.toString(), sessionId, modelConfName, endTime.toString());
			}

			registration.setCurrentEndTime(endTime);
		}

		modelBuildingStore.storeRegistration(registration);
		metrics.store++;
		getSetMetrics(modelConfName).store++;
		getSetMetrics(modelConfName).lastStoredEndTime = convertToSeconds(endTime);
	}

	private ModelBuildingRegistrationServiceSetMetrics getSetMetrics(String setName) {
		if (!setNameToMetrics.containsKey(setName)) {
			setNameToMetrics.put(setName, new ModelBuildingRegistrationServiceSetMetrics(statsService, setName));
		}

		return setNameToMetrics.get(setName);
	}
}
