package fortscale.domain.core;

/**
 * Type of evidence
 * Date: 7/7/2015.
 */
public enum EvidenceType {

	/**
	 * Evidence based on single event
	 */
	AnomalySingleEvent,
	/**
	 * Evidence based on aggregated event
	 */
	AnomalyAggregatedEvent,
	/**
	 * Evidence based on notification
	 */
	Notification
}
