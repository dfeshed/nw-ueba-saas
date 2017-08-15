package presidio.ade.sdk.aggregation_records;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 15/08/2017.
 */
@Configuration
public class AdeManagerSdkBucketConfigurationServiceConfig {
    @Value("${fortscale.ademanager.aggregation.bucket.conf.json.file.name}")
    private String bucketConfJsonFilePath;
    @Value("${fortscale.ademanager.aggregation.bucket.conf.json.overriding.files.path:#{null}}")
    private String bucketConfJsonOverridingFilesPath;
    @Value("${fortscale.ademanager.aggregation.bucket.conf.json.additional.files.path:#{null}}")
    private String bucketConfJsonAdditionalFilesPath;

    @Bean
    public BucketConfigurationService adeManagerSdkBucketConfigurationService() {
        return new BucketConfigurationService(bucketConfJsonFilePath, bucketConfJsonOverridingFilesPath, bucketConfJsonAdditionalFilesPath);
    }
}
