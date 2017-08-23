package fortscale.smart.record.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

import java.util.List;

/**
 * A configuration of one cluster in the smart value calculation. A {@link SmartRecordConf} defines multiple
 * {@link ClusterConf}s. The configuration includes the names of the aggregation records in the cluster and the
 * cluster's weight.
 *
 * @author Lior Govrin
 */
@JsonAutoDetect(
		creatorVisibility = Visibility.ANY,
		fieldVisibility = Visibility.NONE,
		getterVisibility = Visibility.NONE,
		isGetterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE
)
public class ClusterConf {
	private List<String> aggregationRecordNames;
	private Double weight;

	@JsonCreator
	public ClusterConf(
			@JsonProperty("aggregationRecordNames") List<String> aggregationRecordNames,
			@JsonProperty("weight") Double weight) {

		Assert.notEmpty(aggregationRecordNames, "The list of aggregation record names cannot be empty.");
		aggregationRecordNames.forEach(ClusterConf::assertAggregationRecordName);
		// The weight is optional - If it's null, a default weight will be used instead
		if (weight != null) Assert.isTrue(0 <= weight && weight <= 1, "The weight must be in the range [0,1].");

		this.aggregationRecordNames = aggregationRecordNames;
		this.weight = weight;
	}

	public List<String> getAggregationRecordNames() {
		return aggregationRecordNames;
	}

	public void setAggregationRecordNames(List<String> aggregationRecordNames) {
		this.aggregationRecordNames = aggregationRecordNames;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	private static void assertAggregationRecordName(String aggregationRecordName) {
		Assert.hasText(aggregationRecordName, "The list of aggregation record names cannot contain blanks.");
	}
}
