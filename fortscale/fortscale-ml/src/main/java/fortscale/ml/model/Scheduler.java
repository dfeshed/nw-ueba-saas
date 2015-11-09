package fortscale.ml.model;

public interface Scheduler {
	public void register(Registrar registrar, long epochtime);
}
