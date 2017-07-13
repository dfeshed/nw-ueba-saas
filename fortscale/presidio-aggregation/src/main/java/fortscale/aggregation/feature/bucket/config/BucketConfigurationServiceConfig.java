package fortscale.aggregation.feature.bucket.config;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 10/8/16.
 */
@Configuration
public class BucketConfigurationServiceConfig {

    @Value("${impala.table.fields.data.source}")
    private String dataSourceFieldName;
    @Value("${fortscale.aggregation.bucket.conf.json.file.name}")
    private String bucketConfJsonFilePath;
    @Value("${fortscale.aggregation.bucket.conf.json.overriding.files.path:#{null}}")
    private String bucketConfJsonOverridingFilesPath;
    @Value("${fortscale.aggregation.bucket.conf.json.additional.files.path:#{null}}")
    private String bucketConfJsonAdditionalFilesPath;

    @Bean
    @Qualifier("bucketConfigurationService")
    public BucketConfigurationService bucketConfigurationService()
    {
        return new BucketConfigurationService(dataSourceFieldName, bucketConfJsonFilePath, bucketConfJsonOverridingFilesPath,bucketConfJsonAdditionalFilesPath);
    }
}
