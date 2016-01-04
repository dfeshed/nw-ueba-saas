package fortscale.ml.scorer.config;

/**
 * Created by amira on 30/12/2015.
 */
public class MaxScorerContainerConf implements IScorerConf{
    public static final String SCORER_TYPE = "max_scorer_container";

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
