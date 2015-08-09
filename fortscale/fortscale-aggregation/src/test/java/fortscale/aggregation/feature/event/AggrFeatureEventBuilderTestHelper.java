package fortscale.aggregation.feature.event;

import org.springframework.beans.factory.annotation.Value;

public class AggrFeatureEventBuilderTestHelper {

	@Value("${streaming.aggr_event.field.bucket_conf_name}")
    private String bucketConfNameFieldName;
    @Value("${streaming.aggr_event.field.aggregated_feature_name}")
    private String aggrFeatureNameFieldName;
    
    
    
    
	public String getBucketConfNameFieldName() {
		return bucketConfNameFieldName;
	}
	public String getAggrFeatureNameFieldName() {
		return aggrFeatureNameFieldName;
	}
    
    
}
