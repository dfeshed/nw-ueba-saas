package fortscale.streaming.scorer;


import fortscale.common.event.EventMessage;

public interface Scorer {
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception;
}
