package fortscale.ml.scorer.config;

/**
 * Created by amira on 30/12/2015.
 */
public class ContinuousModelScorerConf implements IScorerConf{
    public static final String SCORER_TYPE = "continuous_model_scorer";

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
