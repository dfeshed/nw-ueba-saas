package fortscale.smart.record.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.smart.correlation.conf.CorrelationNodeData;
import fortscale.smart.correlation.conf.FullCorrelation;
import fortscale.utils.Tree;
import fortscale.utils.TreeNode;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import org.springframework.util.Assert;
import presidio.ade.domain.record.aggregated.SmartRecord;

import javax.validation.constraints.AssertTrue;
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
	private Map<String, List<String>> contextToFieldsMap;
	private FixedDurationStrategy fixedDurationStrategy;
	private boolean includeAllAggregationRecords;
	private List<String> excludedAggregationRecords;
	private Double defaultWeight;
	private List<ClusterConf> clusterConfs;
	private Set<String> aggregationRecordNames;
	private List<Tree<CorrelationNodeData>> trees;
	private List<FullCorrelation> fullCorrelations;

	@JsonCreator
	public SmartRecordConf(
			@JsonProperty("name") String name,
			@JsonProperty("contextToFieldsMap") Map<String, List<String>> contextToFieldsMap,
			@JsonProperty("fixedDurationStrategy") String fixedDurationStrategy,
			@JsonProperty("includeAllAggregationRecords") boolean includeAllAggregationRecords,
			@JsonProperty("excludedAggregationRecords") List<String> excludedAggregationRecords,
			@JsonProperty("defaultWeight") Double defaultWeight,
			@JsonProperty("clusterConfs") List<ClusterConf> clusterConfs,
			@JsonProperty("trees") List<Tree<CorrelationNodeData>> trees,
			@JsonProperty("fullCorrelations") List<FullCorrelation> fullCorrelations) {

		this.name = name;
		this.contextToFieldsMap = contextToFieldsMap;
		this.fixedDurationStrategy = FixedDurationStrategy.fromStrategyName(fixedDurationStrategy);
		this.includeAllAggregationRecords = includeAllAggregationRecords;
		this.excludedAggregationRecords = excludedAggregationRecords == null ?
				Collections.emptyList() : excludedAggregationRecords;
		this.defaultWeight = defaultWeight;
		this.clusterConfs = clusterConfs;
		this.trees = trees;
		this.fullCorrelations = fullCorrelations;
		validateArguments();
		initClusterConfs();
		initAggregationRecordNames();
	}

	public String getName() {
		return name;
	}

	public Map<String, List<String>> getContextToFieldsMap() {
		return contextToFieldsMap;
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

	public List<Tree<CorrelationNodeData>> getTrees() {
		return trees;
	}

	public void setTrees(List<Tree<CorrelationNodeData>> trees) {
		this.trees = trees;
	}

	public List<FullCorrelation> getFullCorrelations() {
		return fullCorrelations;
	}

	public void setFullCorrelations(List<FullCorrelation> fullCorrelations) {
		this.fullCorrelations = fullCorrelations;
	}

	private void validateArguments() {
		Assert.hasText(name, "The smart record conf name cannot be blank.");
		Assert.notEmpty(contextToFieldsMap, "The map from context to fields cannot be empty.");
		contextToFieldsMap.forEach((context, fields) -> {
			Assert.hasText(context, "A context cannot be blank.");
			Assert.notEmpty(fields, "A context cannot be mapped to an empty list of fields.");
			fields.forEach(field -> Assert.hasText(field, "A list of fields cannot contain blanks."));
		});

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
