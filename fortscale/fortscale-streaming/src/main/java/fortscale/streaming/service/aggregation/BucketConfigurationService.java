package fortscale.streaming.service.aggregation;

import com.fasterxml.jackson.databind.ObjectMapper;

import fortscale.utils.logging.Logger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.*;

/**
 * Loads BucketConfs from JSON file.
 * Provides API to get list of related BucketConfs for a given event based on the
 * context fields within the BucketConfs.
 */
@Service
public class BucketConfigurationService implements InitializingBean{
    private static final Logger logger = Logger.getLogger(BucketConfigurationService.class);

    public final static String JSON_CONF_BUCKET_CONFS_NODE_NAME = "BucketConfs";
    public final static String EVENT_FIELD_DATA_SOURCE = "data-source";

    private Map<String, FeatureBucketConf> bucketConfs = new HashMap<>();
    private Map<String, List<FeatureBucketConf>> dataSourceToListOfBucketConfs = new HashMap<>();

    @Value("${fortscale.streaming.service.aggregation.bucket_configuration_service.bucket_conf_json}")
    private String bucketConfJsonFilePath;

    @Override
    public void afterPropertiesSet() throws Exception {
        loadBucketConfs();
    }

    private void loadBucketConfs() throws IllegalArgumentException  {
        JSONArray BucketConfsJson;

        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(new FileReader(bucketConfJsonFilePath));
            BucketConfsJson =  (JSONArray)jsonObj.get(JSON_CONF_BUCKET_CONFS_NODE_NAME);
        } catch (Exception e) {
            String errorMsg = String.format("Failed to load BucketConfs from json file %s", bucketConfJsonFilePath);
            logger.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }

        for (Object obj : BucketConfsJson) {
            JSONObject jsonObj = (JSONObject)obj;
            String bucketConfJson = jsonObj.toJSONString();
            try {

                FeatureBucketConf bucketConf = (new ObjectMapper()).readValue(bucketConfJson, FeatureBucketConf.class);
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
            } catch (Exception e) {
                String errorMsg = String.format("Failed to deserialize json %s", bucketConfJson);
                logger.error(errorMsg, e);
                throw new IllegalArgumentException(errorMsg, e);
            }

        }

    }


    public List<FeatureBucketConf> getRelatedBucketConfs(JSONObject event) {

        if(event==null) return null;

        Object dataSourceObj = event.get(EVENT_FIELD_DATA_SOURCE);

        if(dataSourceObj==null) return null;

        String dataSource = dataSourceObj.toString();

        if(dataSource.isEmpty()) return null;

        return dataSourceToListOfBucketConfs.get(dataSource);

    }
}
