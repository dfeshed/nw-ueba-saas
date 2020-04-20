package presidio.ade.sdk.aggregation_records;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Barak Schuster
 */
@Configuration
public class BucketConfigurationServiceConfig {
    @Value("${fortscale.ademanager.aggregation.bucket.conf.json.file.name:classpath:config/asl/feature-buckets/**/**/*.json}")
    private String bucketConfJsonFilePath;
    @Value("${fortscale.ademanager.aggregation.bucket.conf.json.overriding.files.path:#{null}}")
    private String bucketConfJsonOverridingFilesPath;
    @Value("${fortscale.ademanager.aggregation.bucket.conf.json.additional.files.path:#{null}}")
    private String bucketConfJsonAdditionalFilesPath;

    @Bean
    public BucketConfigurationService bucketConfigurationService() {
        return new BucketConfigurationService(bucketConfJsonFilePath, bucketConfJsonOverridingFilesPath, bucketConfJsonAdditionalFilesPath);
    }
}
