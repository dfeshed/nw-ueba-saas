package fortscale.aggregation.feature.bucket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;

import fortscale.utils.logging.Logger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * Loads BucketConfs from JSON file.
 * Provides API to get list of related BucketConfs for a given event based on the
 * context fields within the BucketConfs.
 */
public class BucketConfigurationService implements InitializingBean, ApplicationContextAware{
    private static final Logger logger = Logger.getLogger(BucketConfigurationService.class);

    public final static String JSON_CONF_BUCKET_CONFS_NODE_NAME = "BucketConfs";

    private Map<String, FeatureBucketConf> bucketConfs = new HashMap<>();
    private Map<String, List<FeatureBucketConf>> dataSourceToListOfBucketConfs = new HashMap<>();
    
    @Value("${impala.table.fields.data.source}")
    private String dataSourceFieldName;

    @Value("${fortscale.aggregation.bucket.conf.json.file.name}")
    private String bucketConfJsonFilePath;
    
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        loadBucketConfs();
    }

    private void loadBucketConfs() throws IllegalArgumentException  {
        JSONArray BucketConfsJson;

        try {
            JSONObject jsonObj;
        	Resource bucketsJsonResource = applicationContext.getResource(bucketConfJsonFilePath);
            jsonObj = (JSONObject) JSONValue.parseWithException(bucketsJsonResource.getInputStream());

            BucketConfsJson =  (JSONArray)jsonObj.get(JSON_CONF_BUCKET_CONFS_NODE_NAME);
        } catch (Exception e) {
            String errorMsg = String.format("Failed to load BucketConfs from json file %s", bucketConfJsonFilePath);
            logger.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }

        for (Object obj : BucketConfsJson) {
            JSONObject jsonObj = (JSONObject)obj;
            String bucketConfJson = jsonObj.toJSONString();
            FeatureBucketConf bucketConf = null;
            try {
                bucketConf = (new ObjectMapper()).readValue(bucketConfJson, FeatureBucketConf.class);
            } catch (Exception e) {
                String errorMsg = String.format("Failed to deserialize json %s", bucketConfJson);
                logger.error(errorMsg, e);
                throw new IllegalArgumentException(errorMsg, e);
            }

            try{
            	addNewBucketConf(bucketConf);
            } catch (Exception e) {
                String errorMsg = String.format("Failed to add new bucket conf. json: %s", bucketConfJson);
                logger.error(errorMsg, e);
                throw new IllegalArgumentException(errorMsg, e);
            }
        }

    }


    public List<FeatureBucketConf> getRelatedBucketConfs(JSONObject event) {

        if(event==null) return null;

        Object dataSourceObj = event.get(dataSourceFieldName);

        if(dataSourceObj==null) return null;

        String dataSource = dataSourceObj.toString();

        if(dataSource.isEmpty()) return null;

        return dataSourceToListOfBucketConfs.get(dataSource);

    }

    public FeatureBucketConf getBucketConf(String bucketConfName) {
        return bucketConfs.get(bucketConfName);
    }
    
    public boolean isBucketConfExist(String bucketConfName){
    	return bucketConfs.containsKey(bucketConfName);
    }
    
    public void addNewBucketConf(FeatureBucketConf bucketConf) throws BucketAlreadyExistException{
    	FeatureBucketConf existingBucketConf = getBucketConf(bucketConf.getName());
    	if(existingBucketConf != null){
    		throw new BucketAlreadyExistException(existingBucketConf, bucketConf);
    	}
    	bucketConfs.put(bucketConf.getName(), bucketConf);

        List<String> dataSources = bucketConf.getDataSources();
        for (String s : dataSources) {
            List<FeatureBucketConf> listOfBucketConfs = dataSourceToListOfBucketConfs.get(s);
            if (listOfBucketConfs == null) {
                listOfBucketConfs = new ArrayList<>();
                dataSourceToListOfBucketConfs.put(s, listOfBucketConfs);
            }
            listOfBucketConfs.add(bucketConf);
        }
    }
    
    public void addNewAggregatedFeatureConfToBucketConf(String bucketConfName, AggregatedFeatureConf aggregatedFeatureConf){
    	FeatureBucketConf featureBucketConf = getBucketConf(bucketConfName);
    	featureBucketConf.addAggregatedFeatureConf(aggregatedFeatureConf);
    }
    
    

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
