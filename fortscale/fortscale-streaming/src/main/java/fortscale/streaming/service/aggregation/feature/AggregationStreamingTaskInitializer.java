package fortscale.streaming.service.aggregation.feature;

import fortscale.aggregation.domain.feature.event.FeatureBucketAggrMetadataRepository;
import fortscale.aggregation.domain.feature.event.FeatureBucketAggrSendingQueueRepository;
import fortscale.aggregation.feature.bucket.repository.FeatureBucketMetadataRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


public class AggregationStreamingTaskInitializer implements InitializingBean{

    @Value("${fortscale.aggregation.on.init.delete.all.feature.bucket.metadata}")
    private boolean isDeleteAllFeatureBucketMetadata;

    @Autowired
    private FeatureBucketMetadataRepository featureBucketMetadataRepository;

    @Autowired
    private FeatureBucketAggrMetadataRepository featureBucketAggrMetadataRepository;
    @Autowired
    private FeatureBucketAggrSendingQueueRepository featureBucketAggrSendingQueueRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(isDeleteAllFeatureBucketMetadata) {
            featureBucketMetadataRepository.deleteAll();
            featureBucketAggrMetadataRepository.deleteAll();
            featureBucketAggrSendingQueueRepository.deleteAll();
        }
    }
}
