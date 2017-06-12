package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.FieldValueScoreReducerScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.FieldValueScoreReducerScorerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.recordreader.RecordReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecord;

@Component
public class FieldValueScoreReducerScorerFactory extends AbstractServiceAutowiringFactory<Scorer> {
    private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
            "factoryConfig must be an instance of %s.", FieldValueScoreReducerScorerConf.class.getSimpleName());

    @Autowired
    private FactoryService<RecordReader<AdeRecord>> recordReaderFactoryService;

    @Override
    public String getFactoryName() {
        return FieldValueScoreReducerScorerConf.SCORER_TYPE;
    }

    @Override
    public FieldValueScoreReducerScorer getProduct(FactoryConfig factoryConfig) {
        Assert.isInstanceOf(FieldValueScoreReducerScorerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
        FieldValueScoreReducerScorerConf fieldValueScoreReducerScorerConf = (FieldValueScoreReducerScorerConf) factoryConfig;

        return new FieldValueScoreReducerScorer(fieldValueScoreReducerScorerConf.getName(),
                factoryService.getProduct(fieldValueScoreReducerScorerConf.getBaseScorerConf()),
                fieldValueScoreReducerScorerConf.getLimiters(), recordReaderFactoryService);
    }
}
