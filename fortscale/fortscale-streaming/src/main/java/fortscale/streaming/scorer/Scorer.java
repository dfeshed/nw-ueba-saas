package fortscale.streaming.scorer;



public interface Scorer {
	public Double calculateScore(EventMessage eventMessage) throws Exception;
}
