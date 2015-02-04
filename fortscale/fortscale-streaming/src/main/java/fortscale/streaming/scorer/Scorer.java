package fortscale.streaming.scorer;



public interface Scorer {
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception;
}
