package fortscale.aggregation.feature.bucket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.TreeMultiset;
import fortscale.aggregation.configuration.AslConfigurationService;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Loads {@link FeatureBucketConf}s from JSON files.
 * Provides an API to get a list of related {@link FeatureBucketConf}s for a given event,
 * based on the context fields within the {@link FeatureBucketConf}s.
 */
public class BucketConfigurationService extends AslConfigurationService {
    private static final Logger logger = Logger.getLogger(BucketConfigurationService.class);
    private static final String JSON_CONF_BUCKET_CONFS_NODE_NAME = "BucketConfs";

    private Map<String, FeatureBucketConf> bucketConfs = new HashMap<>();
    private Map<String, List<FeatureBucketConf>> adeEventTypeToListOfBucketConfs = new HashMap<>();
    private Map<FeatureBucketConfCacheKey, List<FeatureBucketConf>> featureBucketConfsCache = new HashMap<>();

    private String bucketConfJsonFilePath;
    private String bucketConfJsonOverridingFilesPath;
    private String bucketConfJsonAdditionalFilesPath;
    private ObjectMapper objectMapper;

    public BucketConfigurationService(
            String bucketConfJsonFilePath,
            String bucketConfJsonOverridingFilesPath,
            String bucketConfJsonAdditionalFilesPath) {

        this.bucketConfJsonFilePath = bucketConfJsonFilePath;
        this.bucketConfJsonOverridingFilesPath = bucketConfJsonOverridingFilesPath;
        this.bucketConfJsonAdditionalFilesPath = bucketConfJsonAdditionalFilesPath;
        this.objectMapper = ObjectMapperProvider.getInstance().getNoModulesObjectMapper();
    }

    @Override
    protected String getBaseConfJsonFilesPath() {
        return bucketConfJsonFilePath;
    }

    @Override
    protected String getBaseOverridingConfJsonFolderPath() {
        return bucketConfJsonOverridingFilesPath;
    }

    @Override
    protected String getAdditionalConfJsonFolderPath() {
        return bucketConfJsonAdditionalFilesPath;
    }

    @Override
    protected String getConfNodeName() {
        return JSON_CONF_BUCKET_CONFS_NODE_NAME;
    }

    @Override
    protected void loadConfJson(JSONObject jsonObj) {
        String bucketConfJson = jsonObj.toJSONString();
        FeatureBucketConf bucketConf;

        try {
            bucketConf = objectMapper.readValue(bucketConfJson, FeatureBucketConf.class);
        } catch (Exception e) {
            String errorMsg = String.format("Failed to deserialize JSON %s.", bucketConfJson);
            logger.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }

        try {
            addNewBucketConf(bucketConf);
        } catch (Exception e) {
            String errorMsg = String.format("Failed to add new configuration. JSON: %s.", bucketConfJson);
            logger.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }

    /**
     * @return A list of {@link FeatureBucketConf}s by adeEventType, strategyName,
     * contextFieldName and context field names excluded by contextFieldNamesToExclude.
     */
    public List<FeatureBucketConf> getRelatedBucketConfs(
            String adeEventType,
            String strategyName,
            String contextFieldName,
            List<String> contextFieldNamesToExclude) {

        if (StringUtils.isEmpty(adeEventType)) return null;
        FeatureBucketConfCacheKey featureBucketConfCacheKey = new FeatureBucketConfCacheKey(strategyName, contextFieldName, adeEventType);
        List<FeatureBucketConf> cachedFeatureBucketConfs = featureBucketConfsCache.computeIfAbsent(featureBucketConfCacheKey, key -> {
            List<FeatureBucketConf> featureBucketConfs = getFeatureBucketConfs(adeEventType);
            return featureBucketConfs == null ? Collections.emptyList() : featureBucketConfs.stream()
                    .filter(conf -> conf.getStrategyName().equals(strategyName) && conf.getContextFieldNames().contains(contextFieldName))
                    .collect(Collectors.toList());
        });

        if (!contextFieldNamesToExclude.isEmpty()) {
            cachedFeatureBucketConfs = cachedFeatureBucketConfs.stream()
                    .filter(conf -> Collections.disjoint(contextFieldNamesToExclude, conf.getContextFieldNames()))
                    .collect(Collectors.toList());
        }

        return cachedFeatureBucketConfs;
    }

	public List<FeatureBucketConf> getFeatureBucketConfs(String adeEventType) {
		List<FeatureBucketConf> ret = adeEventTypeToListOfBucketConfs.get(adeEventType);
		return ret == null ? Collections.emptyList() : ret;
	}

	public List<FeatureBucketConf> getFeatureBucketConfs(String adeEventType, String strategyName) {
		List<FeatureBucketConf> featureBucketConfList = getFeatureBucketConfs(adeEventType);
		if(strategyName != null) {
			featureBucketConfList.stream()
					.filter(featureBucketConf -> featureBucketConf.getStrategyName().equals(strategyName))
					.collect(Collectors.toList());
		}

		return featureBucketConfList;
	}

    public FeatureBucketConf getBucketConf(String bucketConfName) {
        return bucketConfs.get(bucketConfName);
    }

    public Set<List<String>> getRelatedDistinctContexts(String adeEventType) {
        List<FeatureBucketConf> featureBucketConfs = getFeatureBucketConfs(adeEventType);

        if (featureBucketConfs == null) {
            logger.warn("No feature bucket configuration for ADE event type {}.", adeEventType);
            // TODO: Add monitoring metric.
            return Collections.emptySet();
        }

        Set<List<String>> distinctContextsSet = new HashSet<>();

        for (FeatureBucketConf featureBucketConf : featureBucketConfs) {
            distinctContextsSet.add(featureBucketConf.getContextFieldNames());
        }

        return distinctContextsSet;
    }

    private void addNewBucketConf(FeatureBucketConf bucketConf) throws BucketAlreadyExistException {
        FeatureBucketConf existingBucketConf = getBucketConf(bucketConf.getName());
        if (existingBucketConf != null) throw new BucketAlreadyExistException(existingBucketConf, bucketConf);
        bucketConfs.put(bucketConf.getName(), bucketConf);
        List<String> adeEventTypeList = bucketConf.getAdeEventTypes();

        for (String adeEventType : adeEventTypeList) {
            adeEventTypeToListOfBucketConfs.computeIfAbsent(adeEventType, key -> new ArrayList<>()).add(bucketConf);
        }
    }

    private static final class FeatureBucketConfCacheKey {
        private String strategyName;
        private String contextFieldName;
        private String adeEventType;

        public FeatureBucketConfCacheKey(String strategyName, String contextFieldName, String adeEventType) {
            this.strategyName = strategyName;
            this.contextFieldName = contextFieldName;
            this.adeEventType = adeEventType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FeatureBucketConfCacheKey)) return false;
            FeatureBucketConfCacheKey that = (FeatureBucketConfCacheKey)o;
            return new EqualsBuilder()
                    .append(that.strategyName, strategyName)
                    .append(that.contextFieldName, contextFieldName)
                    .append(that.adeEventType, adeEventType)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(strategyName)
                    .append(contextFieldName)
                    .append(adeEventType)
                    .hashCode();
        }
    }
}
