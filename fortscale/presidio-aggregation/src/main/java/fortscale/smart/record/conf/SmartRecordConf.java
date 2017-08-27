package fortscale.smart.record.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A configuration of {@link SmartRecord}s. The configuration includes all
 * the {@link ClusterConf}s that are used when calculating the smart value.
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
	private boolean includeAllAggregationRecords;
	private Double defaultWeight;
	private List<ClusterConf> clusterConfs;
	private Set<String> aggregationRecordNames;

	@JsonCreator
	public SmartRecordConf(
			@JsonProperty("name") String name,
			@JsonProperty("contexts") List<String> contexts,
			@JsonProperty("includeAllAggregationRecords") boolean includeAllAggregationRecords,
			@JsonProperty("defaultWeight") Double defaultWeight,
			@JsonProperty("clusterConfs") List<ClusterConf> clusterConfs) {

		this.name = name;
		this.contexts = contexts;
		this.includeAllAggregationRecords = includeAllAggregationRecords;
		this.defaultWeight = defaultWeight;
		this.clusterConfs = clusterConfs;
		validateArguments();
		initClusterConfs();
		initAggregationRecordNames();
	}

	public String getName() {
		return name;
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

	public List<String> getContexts() {
		return contexts;
	}
}
