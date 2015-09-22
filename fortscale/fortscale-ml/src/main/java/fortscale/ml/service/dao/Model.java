package fortscale.ml.service.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import fortscale.ml.model.prevalance.PrevalanceModel;

@Document(collection="streaming_models")
@CompoundIndexes({
	@CompoundIndex(name="user_model_idx", def = "{'" + Model.MODEL_NAME_FIELD +"': 1, '" + Model.CONTEXT_NAME_FIELD + "': -1}")
})
public class Model {

	public static final String MODEL_NAME_FIELD = "modelName";
	public static final String CONTEXT_NAME_FIELD = "context";
	public static final String MODEL_FIELD = "model";
	
	@Id
	private String id;
	@Field(MODEL_NAME_FIELD)
	private String modelName;
	@Field(CONTEXT_NAME_FIELD)
	private String context;
	@Field(MODEL_FIELD)
	private PrevalanceModel model;
	
	public Model(String modelName, String context, PrevalanceModel model) {
		this.modelName = modelName;
		this.context = context;
		this.model = model;
	}
	
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public PrevalanceModel getModel() {
		return model;
	}
	public void setModel(PrevalanceModel model) {
		this.model = model;
	}	
}
