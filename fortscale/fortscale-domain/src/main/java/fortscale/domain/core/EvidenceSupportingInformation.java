package fortscale.domain.core;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Supporting information for evidence
 * Date: 7/2/2015.
 */
public class EvidenceSupportingInformation {

	// TODO dummy field - place holder for later
	public static final String aggregatedData1Field = "aggregatedData1";

	@Field(aggregatedData1Field)
	private String aggregatedData1;


	//- Getters & Setters

	public String getAggregatedData1() {
		return aggregatedData1;
	}

	public void setAggregatedData1(String aggregatedData1) {
		this.aggregatedData1 = aggregatedData1;
	}
}
