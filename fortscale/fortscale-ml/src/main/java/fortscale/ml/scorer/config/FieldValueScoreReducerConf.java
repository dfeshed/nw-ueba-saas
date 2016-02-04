package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonProperty;

//todo
public class FieldValueScoreReducerConf extends AbstractScorerConf{
    public static final String SCORER_TYPE = "field-value-score-reducer";

    public FieldValueScoreReducerConf(@JsonProperty("name") String name) {
        super(name);
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }
}
