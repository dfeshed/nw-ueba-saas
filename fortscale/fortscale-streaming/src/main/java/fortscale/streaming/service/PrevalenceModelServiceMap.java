package fortscale.streaming.service;

import fortscale.ml.model.prevalance.PrevalanceModel;
import fortscale.ml.service.ModelService;

import java.util.Map;

public class PrevalenceModelServiceMap implements ModelService {
	private Map<String, PrevalanceModelStreamingService> prevalanceModelStreamingServiceMap;

	public PrevalenceModelServiceMap(Map<String, PrevalanceModelStreamingService> prevalanceModelStreamingServiceMap) {
		this.prevalanceModelStreamingServiceMap = prevalanceModelStreamingServiceMap;
	}

	@Override
	public boolean modelExists(String context, String modelName) throws Exception {
		return prevalanceModelStreamingServiceMap.get(modelName).modelExists(context);
	}

	@Override
	public PrevalanceModel getModel(String context, String modelName) throws Exception {
		return prevalanceModelStreamingServiceMap.get(modelName).getModel(context);
	}

	@Override
	public void updateModel(String context, PrevalanceModel model) throws Exception {
		prevalanceModelStreamingServiceMap.get(model.getModelName()).updateModel(context, model);
	}
}
