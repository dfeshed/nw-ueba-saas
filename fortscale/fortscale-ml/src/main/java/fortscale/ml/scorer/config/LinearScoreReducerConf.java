package fortscale.ml.scorer.config;

/**
 * Created by amira on 30/12/2015.
 */
public class LinearScoreReducerConf implements IScorerConf{
    public static final String SCORER_TYPE = "linear_score_reducer";

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
