package fortscale.ml.model;

import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.utils.ConversionUtils;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Configurable(preConstruction = true)
public class ModelService {
	private static final String SESSION_ID_JSON_FIELD = "sessionId";
	private static final String MODEL_CONF_NAME_JSON_FIELD = "modelConfName";
	private static final String END_TIME_IN_SECONDS_JSON_FIELD = "endTimeInSeconds";

	@Autowired
	private ModelConfService modelConfService;

	private IModelBuildingListener modelBuildingListener;
	private Map<String, ModelBuilderManager> modelConfNameToManager;

	public ModelService(IModelBuildingListener modelBuildingListener) {
		Assert.notNull(modelBuildingListener);
		this.modelBuildingListener = modelBuildingListener;

		modelConfNameToManager = new HashMap<>();
		for (ModelConf modelConf : modelConfService.getModelConfs()) {
			ModelBuilderManager modelBuilderManager = new ModelBuilderManager(modelConf);
			modelConfNameToManager.put(modelConf.getName(), modelBuilderManager);
		}
	}

	public void process(JSONObject event) {
		String sessionId = event.getAsString(SESSION_ID_JSON_FIELD);
		String modelConfName = event.getAsString(MODEL_CONF_NAME_JSON_FIELD);
		ModelBuilderManager modelBuilderManager = modelConfNameToManager.get(modelConfName);
		Long endTimeInSeconds = ConversionUtils.convertToLong(event.get(END_TIME_IN_SECONDS_JSON_FIELD));

		if (StringUtils.hasText(sessionId) && modelBuilderManager != null && endTimeInSeconds != null && endTimeInSeconds >= 0) {
			DateTime currentEndTime = new DateTime(TimestampUtils.convertToMilliSeconds(endTimeInSeconds));
			modelBuilderManager.process(modelBuildingListener, sessionId, null, currentEndTime);
		}
	}

	public void window() {}
}
