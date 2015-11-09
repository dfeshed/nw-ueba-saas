package fortscale.streaming.service.state;

/**
 * Definition of the logical step types in the streaming topology
 *
 * @author gils
 * Date: 09/11/2015
 */
public enum StreamingTaskStepType {
    ETL,
    ENRICH,
    SCORE,
    UNDEFINED
}
