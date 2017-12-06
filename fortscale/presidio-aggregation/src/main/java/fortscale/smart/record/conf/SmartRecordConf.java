package fortscale.smart.record.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import org.springframework.util.Assert;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.*;

/**
 * A configuration of {@link SmartRecord}s.
 *
 * @author Lior Govrin
 */
@JsonAutoDetect(
		creatorVisibility = JsonAutoDetect.Visibility.ANY,
		fieldVisibility = JsonAutoDetect.Visibility.NONE,
		getterVisibility = JsonAutoDetect.Visibility.NONE,
		isGetterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class SmartRecordConf {
	private String name;
	private List<String> contexts;
	private FixedDurationStrategy fixedDurationStrategy;
	private boolean includeAllAggregationRecords;
	private List<String> excludedAggregationRecords;
	private Double defaultWeight;
	private List<ClusterConf> clusterConfs;
	private Set<String> aggregationRecordNames;

	@JsonCreator
	public SmartRecordConf(
			@JsonProperty("name") String name,
			@JsonProperty("contexts") List<String> contexts,
			@JsonProperty("fixedDurationStrategy") String fixedDurationStrategy,
			@JsonProperty("includeAllAggregationRecords") boolean includeAllAggregationRecords,
			@JsonProperty("excludedAggregationRecords") List<String> excludedAggregationRecords,
			@JsonProperty("defaultWeight") Double defaultWeight,
			@JsonProperty("clusterConfs") List<ClusterConf> clusterConfs) {

		this.name = name;
		this.contexts = contexts;
		this.fixedDurationStrategy = FixedDurationStrategy.fromStrategyName(fixedDurationStrategy);
		this.includeAllAggregationRecords = includeAllAggregationRecords;
		this.excludedAggregationRecords = excludedAggregationRecords == null ?
				Collections.emptyList() : excludedAggregationRecords;
		this.defaultWeight = defaultWeight;
		this.clusterConfs = clusterConfs;
		validateArguments();
		initClusterConfs();
		initAggregationRecordNames();
	}

	public String getName() {
		return name;
	}

	public List<String> getContexts() {
		return contexts;
	}

	public FixedDurationStrategy getFixedDurationStrategy() {
		return fixedDurationStrategy;
	}

	public boolean isIncludeAllAggregationRecords() {
		return includeAllAggregationRecords;
	}

	public List<String> getExcludedAggregationRecords() {
		return excludedAggregationRecords;
	}

	public Double getDefaultWeight() {
		return defaultWeight;
	}

	public List<ClusterConf> getClusterConfs() {
		return clusterConfs;
	}

	public Set<String> getAggregationRecordNames() {
		return aggregationRecordNames;
	}

	private void validateArguments() {
		Assert.hasText(name, "The smart record conf name cannot be blank.");
		Assert.notEmpty(contexts, "The list of contexts cannot be empty.");
		contexts.forEach(context -> Assert.hasText(context, "The list of contexts cannot contain blanks."));

		if (includeAllAggregationRecords) {
			Assert.isTrue(isDefaultWeightValid(),
					"If all aggregation records are included, a valid default weight must be " +
					"given for the aggregation records that are not defined in the cluster confs.");
		} else {
			Assert.notEmpty(clusterConfs,
					"If not all aggregation records are included, the list of cluster confs cannot be empty.");
		}

		if (!excludedAggregationRecords.isEmpty()) {
			Assert.isTrue(includeAllAggregationRecords, "Excluded aggregation records are allowed only if the " +
					"'includeAllAggregationRecords' flag is on (i.e. singleton clusters are auto-completed).");
		}
	}

	private boolean isDefaultWeightValid() {
		return defaultWeight != null && 0 <= defaultWeight && defaultWeight <= 1;
	}

	private void initClusterConfs() {
		if (clusterConfs == null) {
			clusterConfs = new LinkedList<>();
			return;
		}

		for (int i = 0; i < clusterConfs.size(); i++) {
			ClusterConf clusterConf = clusterConfs.get(i);
			Assert.notNull(clusterConf, "The list of cluster confs cannot contain nulls.");

			if (clusterConf.getWeight() == null) {
				Assert.isTrue(isDefaultWeightValid(), String.format(
						"There should either be a weight in cluster conf number %d, " +
						"or a valid default weight in the smart record conf.", i + 1));
				clusterConf.setWeight(defaultWeight);
			}
		}
	}

	private void initAggregationRecordNames() {
		aggregationRecordNames = new HashSet<>();

		for (ClusterConf clusterConf : clusterConfs) {
			for (String aggregationRecordName : clusterConf.getAggregationRecordNames()) {
				if (aggregationRecordNames.contains(aggregationRecordName)) {
					String s = String.format("%s is defined multiple times.", aggregationRecordName);
					throw new IllegalArgumentException(s);
				}

				aggregationRecordNames.add(aggregationRecordName);
			}
		}
	}
}
