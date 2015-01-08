package fortscale.ml.model.prevalance;

public interface PrevalanceModelBuilder {
	public PrevalanceModelBuilderImpl withField(String fieldName, String fieldModelClassName, String scoreBoostClassName);
	public PrevalanceModel build() throws Exception;
	public String getModelName();
}
