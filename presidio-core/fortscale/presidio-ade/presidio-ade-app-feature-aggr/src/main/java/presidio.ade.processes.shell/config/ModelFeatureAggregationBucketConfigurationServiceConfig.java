package presidio.ade.processes.shell.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 7/30/17.
 */
@Configuration
public class ModelFeatureAggregationBucketConfigurationServiceConfig {
    @Value("${fortscale.model.aggregation.bucket.conf.json.file.name}")
    private String bucketConfJsonFilePath;
    @Value("${fortscale.model.aggregation.bucket.conf.json.overriding.files.path:#{null}}")
    private String bucketConfJsonOverridingFilesPath;
    @Value("${fortscale.model.aggregation.bucket.conf.json.additional.files.path:#{null}}")
    private String bucketConfJsonAdditionalFilesPath;

    @Bean
    @Qualifier("modelBucketConfigService")
    public BucketConfigurationService modelBucketConfigService()
    {
        return new BucketConfigurationService(bucketConfJsonFilePath, bucketConfJsonOverridingFilesPath,bucketConfJsonAdditionalFilesPath);
    }
}
