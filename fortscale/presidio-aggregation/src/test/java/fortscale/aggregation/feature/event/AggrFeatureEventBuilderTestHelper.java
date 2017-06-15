package fortscale.aggregation.feature.event;

import org.springframework.beans.factory.annotation.Value;

public class AggrFeatureEventBuilderTestHelper {

	@Value("${streaming.aggr_event.field.bucket_conf_name}")
    private String bucketConfNameFieldName;
    @Value("${streaming.aggr_event.field.aggregated_feature_name}")
    private String aggrFeatureNameFieldName;
    @Value("${streaming.aggr_event.field.aggregated_feature_value}")
    private String aggrFeatureValueFieldName;
    @Value("${streaming.aggr_event.field.context}")
	private String contextFieldName;
    
    
    
    
	public String getBucketConfNameFieldName() {
		return bucketConfNameFieldName;
	}
	public String getAggrFeatureNameFieldName() {
		return aggrFeatureNameFieldName;
	}
	public String getAggrFeatureNameFieldValue() {
		return aggrFeatureValueFieldName;
	}
    
	public String getAggrFeatureContextFieldName(){
		return contextFieldName;
	}
}
