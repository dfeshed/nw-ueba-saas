package fortscale.ml.scorer.config;

/**
 * Created by amira on 30/12/2015.
 */
public class DiscreetValuesModelScorerConf implements IScorerConf{
    public static final String SCORER_TYPE = "discreet_values_model_scorer";

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
