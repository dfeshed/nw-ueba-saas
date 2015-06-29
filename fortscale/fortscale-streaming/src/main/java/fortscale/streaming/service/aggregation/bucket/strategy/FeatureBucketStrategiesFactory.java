package fortscale.streaming.service.aggregation.bucket.strategy;

import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.JsonMappingException;

import fortscale.utils.logging.Logger;

public class FeatureBucketStrategiesFactory implements InitializingBean{
	private static final Logger logger = Logger.getLogger(FeatureBucketStrategiesFactory.class);
	
	
	
	private static final String	JSON_CONF_STRATEGY_CONFS_NODE_NAME = 	"BucketStrategies";
	private static final String	JSON_CONF_STRATEGIES_CONFS_FIELD_NAME = 	"Strategies";
	
	
	@Value("${fortscale.aggregation.feature.strategy.conf.json.file.name}")
	private String confJsonFileName;
	
	
	
	

	private Map<String,StrategyJson> strategyNameToStrategyJsonObjectMap = new HashMap<>();
	private Map<String, FeatureBucketStrategy> featureBucketStrategyMap = new HashMap<>();
	protected Map<String, FeatureBucketStrategyFactory> featureBucketStrategyFactoryMap = new HashMap<>();
	private JSONObject bucketStrategiesConfJson;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		setConfJsonFromFile(confJsonFileName);
	}
	
	public void registerFeatureBucketStrategyFactory(String strategyType, FeatureBucketStrategyFactory featureBucketStrategyFactory){
		featureBucketStrategyFactoryMap.put(strategyType, featureBucketStrategyFactory);
	}

	/**
	 *
	 * @param confJson must include a "FeatureConfs" node with feature definitions
	 * @throws JsonMappingException 
	 */
	private void initAslConfJson(JSONObject confJson) throws JsonMappingException {
		Assert.notNull(confJson);
		initBucketStrategiesConfJson((JSONObject)confJson.get(JSON_CONF_STRATEGY_CONFS_NODE_NAME));
	}
	
	private void initBucketStrategiesConfJson(JSONObject bucketStrategiesConfJson) throws JsonMappingException{
		Assert.notNull(bucketStrategiesConfJson, "did not get json object for feature bucket strategies configuration");
		this.bucketStrategiesConfJson = bucketStrategiesConfJson;
		JSONArray strategies = (JSONArray) this.bucketStrategiesConfJson.get(JSON_CONF_STRATEGIES_CONFS_FIELD_NAME);
		Iterator<Object> iter = strategies.iterator();
		while(iter.hasNext()){
			StrategyJson strategyJson = new StrategyJson((JSONObject) iter.next());
			strategyNameToStrategyJsonObjectMap.put(strategyJson.getName(), strategyJson);
		}
		
		createAllStrategies();
	}
	
	protected void setConfJsonFromFile(String fileName) throws IllegalArgumentException {
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(new FileReader(fileName));
			initAslConfJson(jsonObj);
		} catch (Exception e) {
			String errorMsg = String.format("Failed to read json conf file %s", fileName);
			logger.error(errorMsg, e);
			throw new IllegalArgumentException(errorMsg, e);
		}
	}

	public FeatureBucketStrategy getFeatureBucketStrategy(String strategyName) {
		FeatureBucketStrategy ret = featureBucketStrategyMap.get(strategyName);
		if(ret==null) {
			ret = createFeatureBucketStrategy(strategyName);
		}
		return ret;
	}
	
	public Collection<FeatureBucketStrategy> getAllStrategies(){
		return featureBucketStrategyMap.values();
	}
	
	private void createAllStrategies(){
		for(String strategyName: strategyNameToStrategyJsonObjectMap.keySet()){
			createFeatureBucketStrategy(strategyName);
		}
	}

	private FeatureBucketStrategy createFeatureBucketStrategy(String strategyName) {
		FeatureBucketStrategy ret = null;
		StrategyJson strategyJson = strategyNameToStrategyJsonObjectMap.get(strategyName);

		if(strategyJson==null) {
			logger.error("No such strategy name: {}", strategyName);
			return null;
		}

		FeatureBucketStrategyFactory featureBucketStrategyFactory = featureBucketStrategyFactoryMap.get(strategyJson.getType());
		
		try {
			ret = featureBucketStrategyFactory.createFeatureBucketStrategy(strategyJson);
			featureBucketStrategyMap.put(strategyName, ret);
		} catch (JsonMappingException e) {
			logger.error("got exception while trying to create feature bucket strategy");
		}
		
		return ret;

	}
	
	


	
}
