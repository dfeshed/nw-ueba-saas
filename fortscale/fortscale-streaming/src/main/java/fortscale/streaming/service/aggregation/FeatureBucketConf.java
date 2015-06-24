package fortscale.streaming.service.aggregation;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import java.io.Serializable;
import java.util.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class FeatureBucketConf implements Serializable{
	private static final long serialVersionUID = 1L;

	private String name;
	private List<String> dataSources = new ArrayList<>();
	private List<String> contextFieldNames = new ArrayList<>();
	private String strategyName;
	private List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
	private Set<String> allFeatureNames = new HashSet<>();

    public FeatureBucketConf(@JsonProperty("name")String name,
                             @JsonProperty("dataSources")List<String> dataSources,
                             @JsonProperty("contextFieldNames")List<String> contextFieldNames,
                             @JsonProperty("strategyName")String strategyName,
                             @JsonProperty("aggrFeatureConfs")List<AggregatedFeatureConf> aggrFeatureConfs) {

        Assert.notNull(name);
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

        this.name = name;
        this.dataSources = dataSources;
		this.contextFieldNames = contextFieldNames;
		this.strategyName = strategyName;
		this.aggrFeatureConfs = aggrFeatureConfs;

		for (AggregatedFeatureConf conf : aggrFeatureConfs) {
			allFeatureNames.addAll(conf.getFeatureNames());
		}
	}


	public String getName() { return name; }

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
