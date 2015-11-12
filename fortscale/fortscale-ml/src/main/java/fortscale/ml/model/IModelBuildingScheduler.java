package fortscale.ml.model;

public interface IModelBuildingScheduler {
	public void register(IModelBuildingRegistrar registrar, long epochtime);
}
