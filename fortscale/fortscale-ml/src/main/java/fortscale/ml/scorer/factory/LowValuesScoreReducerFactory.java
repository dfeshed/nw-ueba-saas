package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.LowValuesScoreReducer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.LowValuesScoreReducerConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.recordreader.RecordReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecord;

@SuppressWarnings("unused")
@Component
public class LowValuesScoreReducerFactory extends AbstractServiceAutowiringFactory<Scorer> {
	private static final String FACTORY_CONFIG_TYPE_ERROR_MSG = String.format(
			"factoryConfig must be an instance of %s.", LowValuesScoreReducerConf.class.getSimpleName());
	@Autowired
	private FactoryService<RecordReader<AdeRecord>> recordReaderFactoryService;

	@Override
	public String getFactoryName() {
		return LowValuesScoreReducerConf.SCORER_TYPE;
	}

	@Override
	public Scorer getProduct(FactoryConfig factoryConfig) {
		Assert.isInstanceOf(LowValuesScoreReducerConf.class, factoryConfig, FACTORY_CONFIG_TYPE_ERROR_MSG);
		LowValuesScoreReducerConf conf = (LowValuesScoreReducerConf)factoryConfig;
		Scorer baseScorer = factoryService.getProduct(conf.getBaseScorerConf());
		return new LowValuesScoreReducer(conf.getName(), baseScorer, conf.getReductionConfigs(), recordReaderFactoryService);
	}
}
