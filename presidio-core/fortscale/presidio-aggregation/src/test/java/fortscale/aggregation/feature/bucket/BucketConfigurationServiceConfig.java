package fortscale.aggregation.feature.bucket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BucketConfigurationServiceConfig {
    @Value("${fortscale.aggregation.bucket.conf.json.file.name}")
    private String bucketConfJsonFilePath;
    @Value("${fortscale.aggregation.bucket.conf.json.overriding.files.path:#{null}}")
    private String bucketConfJsonOverridingFilesPath;
    @Value("${fortscale.aggregation.bucket.conf.json.additional.files.path:#{null}}")
    private String bucketConfJsonAdditionalFilesPath;

    @Bean
    public BucketConfigurationService bucketConfigurationService() {
        return new BucketConfigurationService(bucketConfJsonFilePath, bucketConfJsonOverridingFilesPath, bucketConfJsonAdditionalFilesPath);
    }
}
