package fortscale.aggregation.feature.bucket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class FeatureBucketConf implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FeatureBucketConf.class);

	private String name;
	private List<String> adeEventTypes;
	private List<String> contextFieldNames;
	private String strategyName;
	private Integer expireAfterSeconds;
	private List<AggregatedFeatureConf> aggrFeatureConfs;
	private Set<String> allFeatureNames;

	public FeatureBucketConf(
			@JsonProperty("name") String name,
			@JsonProperty("adeEventTypes") List<String> adeEventTypes,
			@JsonProperty("contextFieldNames") List<String> contextFieldNames,
			@JsonProperty("strategyName") String strategyName,
			@JsonProperty("aggrFeatureConfs") List<AggregatedFeatureConf> aggrFeatureConfs) {

		Assert.isTrue(StringUtils.isNotBlank(name));
		Assert.notEmpty(adeEventTypes);
		for (String adeEventType : adeEventTypes) {
			Assert.isTrue(StringUtils.isNotBlank(adeEventType));
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
		this.adeEventTypes = adeEventTypes;
		this.contextFieldNames = contextFieldNames;
		this.strategyName = strategyName;
		this.allFeatureNames = new HashSet<>();
		this.aggrFeatureConfs = new ArrayList<>();
		addAllAggregatedFeatureConfs(aggrFeatureConfs);
	}

	public String getName() {
		return name;
	}

	public List<String> getAdeEventTypes() {
		return adeEventTypes;
	}

	public List<String> getContextFieldNames() {
		return contextFieldNames;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public Integer getExpireAfterSeconds() {
		return expireAfterSeconds;
	}

	public List<AggregatedFeatureConf> getAggrFeatureConfs() {
		return aggrFeatureConfs;
	}

	public AggregatedFeatureConf getAggregatedFeatureConf(String name) {
		for(AggregatedFeatureConf aggregatedFeatureConf: aggrFeatureConfs) {
			if(aggregatedFeatureConf.getName().equals(name)) {
				return aggregatedFeatureConf;
			}
		}
		return null;
	}

	public Set<String> getAllFeatureNames() {
		return allFeatureNames;
	}
	
	public void addAllAggregatedFeatureConfs(List<AggregatedFeatureConf> aggrFeatureConfs){
		for (AggregatedFeatureConf conf : aggrFeatureConfs) {
			addAggregatedFeatureConf(conf);
		}
	}
	
	public void addAggregatedFeatureConf(AggregatedFeatureConf aggrFeatureConf){
		this.aggrFeatureConfs.add(aggrFeatureConf);
		allFeatureNames.addAll(aggrFeatureConf.getAllFeatureNames());
	}
	
	@Override
	public String toString(){
		ObjectMapper mapper = ObjectMapperProvider.getInstance().getDefaultObjectMapper();

		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			logger.warn("failed to serialize to json", e);
			return super.toString();
		}
	}
}
