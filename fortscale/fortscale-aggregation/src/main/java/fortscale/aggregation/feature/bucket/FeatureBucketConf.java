package fortscale.aggregation.feature.bucket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class FeatureBucketConf implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private List<String> dataSources;
	private List<String> contextFieldNames;
	private String strategyName;
	private List<AggregatedFeatureConf> aggrFeatureConfs;
	private Set<String> allFeatureNames;

	public FeatureBucketConf(
			@JsonProperty("name") String name,
			@JsonProperty("dataSources") List<String> dataSources,
			@JsonProperty("contextFieldNames") List<String> contextFieldNames,
			@JsonProperty("strategyName") String strategyName,
			@JsonProperty("aggrFeatureConfs") List<AggregatedFeatureConf> aggrFeatureConfs) {

		Assert.isTrue(StringUtils.isNotBlank(name));
		Assert.notEmpty(dataSources);
		for (String dataSource : dataSources) {
			Assert.isTrue(StringUtils.isNotBlank(dataSource));
		}
		Assert.notEmpty(contextFieldNames);
		for (String contextFieldName : contextFieldNames) {
			Assert.isTrue(StringUtils.isNotBlank(contextFieldName));
		}
		Assert.isTrue(StringUtils.isNotBlank(strategyName));
		Assert.notEmpty(aggrFeatureConfs);
		for (AggregatedFeatureConf conf : aggrFeatureConfs) {
			Assert.notNull(conf);
		}

		this.name = name;
		this.dataSources = dataSources;
		this.contextFieldNames = contextFieldNames;
		this.strategyName = strategyName;
		this.aggrFeatureConfs = aggrFeatureConfs;
		this.allFeatureNames = new HashSet<>();
		for (AggregatedFeatureConf conf : aggrFeatureConfs) {
			allFeatureNames.addAll(conf.getAllFeatureNames());
		}
	}

	public String getName() {
		return name;
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

	public List<AggregatedFeatureConf> getAggrFeatureConfs() {
		return aggrFeatureConfs;
	}

	public Set<String> getAllFeatureNames() {
		return allFeatureNames;
	}
}
