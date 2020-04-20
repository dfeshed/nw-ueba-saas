package presidio.ade.processes.shell.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeatureAggregationBucketConfigurationServiceConfig {
    @Value("${fortscale.feature.aggregation.bucket.conf.json.file.name}")
    private String bucketConfJsonFilePath;
    @Value("${fortscale.feature.aggregation.bucket.conf.json.overriding.files.path:#{null}}")
    private String bucketConfJsonOverridingFilesPath;
    @Value("${fortscale.feature.aggregation.bucket.conf.json.additional.files.path:#{null}}")
    private String bucketConfJsonAdditionalFilesPath;

    @Bean
    @Qualifier("bucketConfigurationService")
    public BucketConfigurationService bucketConfigurationService() {
        return new BucketConfigurationService(bucketConfJsonFilePath, bucketConfJsonOverridingFilesPath, bucketConfJsonAdditionalFilesPath);
    }
}