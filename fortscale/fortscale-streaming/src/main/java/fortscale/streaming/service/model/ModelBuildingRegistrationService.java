package fortscale.streaming.service.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.ModelService;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.message.ModelBuildingCommandMessage;
import fortscale.streaming.service.model.metrics.ModelBuildingRegistrationServiceMetrics;
import fortscale.streaming.service.model.metrics.ModelBuildingRegistrationServiceSetMetrics;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fortscale.utils.time.TimestampUtils.convertToMilliSeconds;
import static fortscale.utils.time.TimestampUtils.convertToSeconds;

@Configurable(preConstruction = true)
public class ModelBuildingRegistrationService {
	private static final Logger logger = Logger.getLogger(ModelBuildingRegistrationService.class);
	private final Pattern allModelsFilterRegexPattern;
	private final ObjectMapper objectMapper;

	@Autowired
	private ModelConfService modelConfService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private StatsService statsService;

	@Value("${fortscale.model.build.message.constant.all.models}")
	private String allModelsConstantValue;

	private IModelBuildingListener modelBuildingListener;
	private ModelBuildingSamzaStore modelBuildingStore;
	private ModelBuildingRegistrationServiceMetrics metrics;
	private Map<String, ModelBuildingRegistrationServiceSetMetrics> setNameToMetrics;

	public ModelBuildingRegistrationService(
			IModelBuildingListener modelBuildingListener,
			ModelBuildingSamzaStore modelBuildingStore, String allModelsFilterRegex) {

		// modelBuildingListener can be null, if there is no listener
		Assert.notNull(modelBuildingStore);
		this.modelBuildingListener = modelBuildingListener;
		this.modelBuildingStore = modelBuildingStore;
		this.metrics = new ModelBuildingRegistrationServiceMetrics(statsService);
		this.setNameToMetrics = new HashMap<>();
		objectMapper = new ObjectMapper().registerModule(new JsonOrgModule());

		if(allModelsFilterRegex!=null) {
			this.allModelsFilterRegexPattern = Pattern.compile(allModelsFilterRegex);
		}
		else
		{
			this.allModelsFilterRegexPattern = null;
		}
		modelService.init();
	}

	public void process(JSONObject event) {
		ModelBuildingCommandMessage modelBuildingCommandMessage = objectMapper.convertValue(event,ModelBuildingCommandMessage.class);

		String sessionId = modelBuildingCommandMessage.getSessionId();
		String modelConfName = modelBuildingCommandMessage.getModelConfName();
		Long endTimeSec = modelBuildingCommandMessage.getEndTimeInSeconds();

		if (StringUtils.hasText(sessionId) && StringUtils.hasText(modelConfName)) {
			metrics.processed++;
			getSetMetrics(modelConfName).processed++; // TODO: modelConfName can be "ALL_MODELS" and later on "RAW_EVENT_MODELS" for example
			Date endTime = endTimeSec < 0 ? null : new Date(convertToMilliSeconds(endTimeSec));

			if (modelConfName.equalsIgnoreCase(allModelsConstantValue)) {
				for (ModelConf modelConf : modelConfService.getModelConfs()) {
					String currentModelConfName = modelConf.getName();
					// modelConfName can be "ALL_MODELS", you may filter it by regex
					if(allModelsFilterRegexPattern != null)
					{
						Matcher matcher = allModelsFilterRegexPattern.matcher(currentModelConfName);
						if(matcher.matches())
						{
							process(sessionId, currentModelConfName, endTime);
						}
					}
					else {
						process(sessionId, currentModelConfName, endTime);
					}
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
