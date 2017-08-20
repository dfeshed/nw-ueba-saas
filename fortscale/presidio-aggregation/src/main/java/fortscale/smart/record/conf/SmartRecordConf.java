package fortscale.smart.record.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A POJO for smart record confs.
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
	private double defaultWeight;
	private List<ClusterConf> clusterConfs;
	private Set<String> aggregationRecordNames;

	@JsonCreator
	public SmartRecordConf(
			@JsonProperty("name") String name,
			@JsonProperty("contexts") List<String> contexts,
			@JsonProperty("includeAllAggregationRecords") boolean includeAllAggregationRecords,
			@JsonProperty("defaultWeight") double defaultWeight,
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
			Assert.isTrue(defaultWeight > 0,
					"If all aggregation records are included, a positive default weight must be " +
					"given for the aggregation records that are not defined in the cluster confs.");
		} else {
			Assert.notEmpty(clusterConfs,
					"If not all aggregation records should be included, " +
					"the list of cluster confs cannot be empty."); // TODO
		}
	}

	private void initClusterConfs() {
		if (clusterConfs == null) {
			clusterConfs = new LinkedList<>();
			return;
		}

		for (int i = 0; i < clusterConfs.size(); i++) {
			ClusterConf clusterConf = clusterConfs.get(i);
			Assert.notNull(clusterConf, "The list of cluster confs cannot contain nulls.");

			if (clusterConf.getWeight() <= 0) {
				Assert.isTrue(defaultWeight > 0, String.format(
						"Cluster conf number %d does not include a positive weight, and " +
						"the smart record conf is missing a positive default weight.", i + 1));
				clusterConf.setWeight(defaultWeight);
			}
		}
	}

	private void initAggregationRecordNames() {
		aggregationRecordNames = new HashSet<>();

		for (ClusterConf clusterConf : clusterConfs) {
			for (String aggregationRecordName : clusterConf.getAggregationRecordNames()) {
				Assert.isTrue(!aggregationRecordNames.contains(aggregationRecordName), String.format(
						"%s is defined multiple times.", aggregationRecordName));
				aggregationRecordNames.add(aggregationRecordName);
			}
		}
	}
}
