package fortscale.smart.record.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.Assert;

import java.util.ArrayList;
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

	//This empty constructor is needed for mongo
	public ClusterConf(){}

	@JsonCreator
	public ClusterConf(
			@JsonProperty("aggregationRecordNames") List<String> aggregationRecordNames,
			@JsonProperty("weight") Double weight) {
		setAggregationRecordNames(aggregationRecordNames);
		setWeight(weight);
	}

	public ClusterConf(ClusterConf other) {
		this.aggregationRecordNames = new ArrayList<>(other.getAggregationRecordNames());
		this.weight = new Double(other.getWeight());
	}

	public List<String> getAggregationRecordNames() {
		return aggregationRecordNames;
	}

	public void setAggregationRecordNames(List<String> aggregationRecordNames) {
		Assert.notEmpty(aggregationRecordNames, "The list of aggregation record names cannot be empty.");
		aggregationRecordNames.forEach(ClusterConf::assertAggregationRecordName);
		this.aggregationRecordNames = aggregationRecordNames;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		// The weight is optional - If it's null, a default weight will be used instead
		if (weight != null) Assert.isTrue(0 <= weight && weight <= 1, "The weight must be in the range [0,1].");
		this.weight = weight;
	}

	private static void assertAggregationRecordName(String aggregationRecordName) {
		Assert.hasText(aggregationRecordName, "The list of aggregation record names cannot contain blanks.");
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;

		ClusterConf other = (ClusterConf) o;

		return new EqualsBuilder()
				.append(aggregationRecordNames, other.aggregationRecordNames)
				.append(weight, other.weight)
				.build();
	}

	@Override
	public int hashCode() {
		return  new HashCodeBuilder()
				.append(aggregationRecordNames)
				.append(weight)
				.build();
	}

	@Override
	public String toString() {
		return String.format("[%s] -> %f", String.join(", ", aggregationRecordNames), weight);
	}
}
