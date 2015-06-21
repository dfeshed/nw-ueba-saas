package fortscale.streaming.service.aggregation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FeatureBucketConf implements Serializable{
	private static final long serialVersionUID = 1L;

	private List<String> dataSources;
	private List<String> contextFieldNames;
	private String strategyName;
	private Map<String, AggregatedFeatureConf> aggregatedFeatureConfs;
	private Set<String> allFeatureNames;

	public FeatureBucketConf(List<String> dataSources, List<String> contextFieldNames, String strategyName, Map<String, AggregatedFeatureConf> aggregatedFeatureConfs) {
		// Validate input
		Assert.notEmpty(dataSources);
		for (String dataSource : dataSources) {
			Assert.isTrue(StringUtils.isNotBlank(dataSource));
		}
		Assert.notEmpty(contextFieldNames);
		for (String contextFieldName : contextFieldNames) {
			Assert.isTrue(StringUtils.isNotBlank(contextFieldName));
		}
		Assert.isTrue(StringUtils.isNotBlank(strategyName));
		Assert.notEmpty(aggregatedFeatureConfs);
		for (Map.Entry<String, AggregatedFeatureConf> entry : aggregatedFeatureConfs.entrySet()) {
			Assert.isTrue(StringUtils.isNotBlank(entry.getKey()));
			Assert.notNull(entry.getValue());
		}

		this.dataSources = dataSources;
		this.contextFieldNames = contextFieldNames;
		this.strategyName = strategyName;
		this.aggregatedFeatureConfs = aggregatedFeatureConfs;

		allFeatureNames = new HashSet<>();
		for (AggregatedFeatureConf conf : aggregatedFeatureConfs.values()) {
			allFeatureNames.addAll(conf.getFeatureNames());
		}
	}

	public List<String> getDataSources() {
		return dataSources;
	}

	public List<String> getContextFieldNames() {
		return contextFieldNames;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public Map<String, AggregatedFeatureConf> getAggregatedFeatureConfs() {
		return aggregatedFeatureConfs;
	}

	public Set<String> getAllFeatureNames() {
		return allFeatureNames;
	}
}
