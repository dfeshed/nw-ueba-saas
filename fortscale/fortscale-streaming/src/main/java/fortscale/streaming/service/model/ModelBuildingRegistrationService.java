package fortscale.streaming.service.model;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.ModelService;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.utils.ConversionUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Iterator;

@Configurable(preConstruction = true)
public class ModelBuildingRegistrationService {
	private static final String SESSION_ID_JSON_FIELD = "sessionId";
	private static final String MODEL_CONF_NAME_JSON_FIELD = "modelConfName";
	private static final String END_TIME_IN_SECONDS_JSON_FIELD = "endTimeInSeconds";
	private static final String ALL_MODELS_CONSTANT_VALUE = "ALL_MODELS";
	private static final Logger logger = Logger.getLogger(ModelBuildingRegistrationService.class);

	@Autowired
	private ModelConfService modelConfService;
	@Autowired
	private ModelService modelService;

	private IModelBuildingListener modelBuildingListener;
	private ModelBuildingSamzaStore modelBuildingStore;

	public ModelBuildingRegistrationService(
			IModelBuildingListener modelBuildingListener,
			ModelBuildingSamzaStore modelBuildingStore) {

		// modelBuildingListener can be null, if there is no listener
		Assert.notNull(modelBuildingStore);
		this.modelBuildingListener = modelBuildingListener;
		this.modelBuildingStore = modelBuildingStore;
	}

	public void process(JSONObject event) {
		String sessionId = event.getAsString(SESSION_ID_JSON_FIELD);
		String modelConfName = event.getAsString(MODEL_CONF_NAME_JSON_FIELD);
		Long endTimeSec = ConversionUtils.convertToLong(event.get(END_TIME_IN_SECONDS_JSON_FIELD));

		if (StringUtils.hasText(sessionId) && StringUtils.hasText(modelConfName) && endTimeSec != null) {
			Date endTime = endTimeSec < 0 ? null : new Date(TimestampUtils.convertToMilliSeconds(endTimeSec));

			if (modelConfName.equalsIgnoreCase(ALL_MODELS_CONSTANT_VALUE)) {
				for (ModelConf modelConf : modelConfService.getModelConfs()) {
					process(sessionId, modelConf.getName(), endTime);
				}
			} else {
				process(sessionId, modelConfName, endTime);
			}
		} else {
			logger.error("Ignoring message with invalid arguments: {}.", event.toJSONString());
		}
	}

	public void window() {
		Iterator<ModelBuildingRegistration> iterator = modelBuildingStore.getRegistrationsIterator();

		while (iterator.hasNext()) {
			ModelBuildingRegistration reg = iterator.next();

			if (reg.getCurrentEndTime() != null) {
				modelService.process(modelBuildingListener, reg.getSessionId(), reg.getModelConfName(),
						reg.getPreviousEndTime(), reg.getCurrentEndTime());
				reg.setPreviousEndTime(reg.getCurrentEndTime());
				reg.setCurrentEndTime(null);
			}
		}
	}

	private void process(String sessionId, String modelConfName, Date endTime) {
		if (endTime == null) {
			modelBuildingStore.deleteRegistration(sessionId, modelConfName);
			return;
		}

		ModelBuildingRegistration registration = modelBuildingStore.getRegistration(sessionId, modelConfName);

		if (registration == null) {
			registration = new ModelBuildingRegistration(sessionId, modelConfName, null, endTime);
		} else {
			Date currentEndTime = registration.getCurrentEndTime();

			if (currentEndTime != null && currentEndTime.after(endTime)) {
				logger.warn("Overwriting later end time {} for session {} and model conf {} with earlier end time {}.",
						currentEndTime.toString(), sessionId, modelConfName, endTime.toString());
			}

			registration.setCurrentEndTime(endTime);
		}

		modelBuildingStore.storeRegistration(registration);
	}
}
