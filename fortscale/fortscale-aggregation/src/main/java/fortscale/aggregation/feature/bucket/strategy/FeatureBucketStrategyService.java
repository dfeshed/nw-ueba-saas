package fortscale.aggregation.feature.bucket.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.common.event.Event;
import fortscale.utils.ConversionUtils;
import fortscale.utils.time.TimestampUtils;


public abstract class FeatureBucketStrategyService {
	
	@Value("${impala.table.fields.epochtime}")
	private String epochtimeFieldName;

	public List<FeatureBucketStrategyData> updateStrategies(Event event) {
		List<FeatureBucketStrategyData> ret = new ArrayList<>();
		for(FeatureBucketStrategy strategy: getAllStrategies()){
			FeatureBucketStrategyData updatedStrategyData = strategy.update(event);
			if(updatedStrategyData != null){
				ret.add(updatedStrategyData);
			}
		}

		return ret;
	}
	
	public List<FeatureBucketStrategyData> getFeatureBucketStrategyData(Event event, FeatureBucketConf featureBucketConf){
		FeatureBucketStrategy strategy = getFeatureBucketStrategy(featureBucketConf.getStrategyName());
		Long epochtimeInSec = ConversionUtils.convertToLong(event.get(epochtimeFieldName));
		if(epochtimeInSec!=null){
			epochtimeInSec = TimestampUtils.convertToSeconds(epochtimeInSec);
			return strategy.getFeatureBucketStrategyData(featureBucketConf, event, epochtimeInSec);
		}
		return null;
	}

	private FeatureBucketStrategy getFeatureBucketStrategy(String strategyName) {
		return getFeatureBucketStrategiesFactory().getFeatureBucketStrategy(strategyName);
	}
	
	private Collection<FeatureBucketStrategy> getAllStrategies(){
		return getFeatureBucketStrategiesFactory().getAllStrategies();
	}

	public abstract FeatureBucketStrategiesFactory getFeatureBucketStrategiesFactory();

	public abstract FeatureBucketsStore getFeatureBucketsStore();
	
	
}
