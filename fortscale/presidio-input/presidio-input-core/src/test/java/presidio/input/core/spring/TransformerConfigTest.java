package presidio.input.core.spring;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import java.time.Duration;
import java.util.Properties;

@Configuration
@EnableSpringConfigured
public class TransformerConfigTest {
    @Bean
    public static TestPropertiesPlaceholderConfigurer inputCoreTestConfigurer() {
        Properties properties = new Properties();
        properties.put("dataPipeline.startTime", "2019-01-01T00:00:00Z");
        properties.put("presidio.last.occurrence.instant.reader.maximum.size", 200000);
        properties.put("presidio.last.occurrence.instant.reader.load.factor", 0.9);
        properties.put("presidio.last.occurrence.instant.reader.entries.to.remove.percentage", 10.0);
        properties.put("presidio.input.core.transformation.waiting.duration", Duration.ZERO.toString());
        properties.put("folder.operation.types", "LOCAL_SHARE_FOLDER_PATH_CHANGED,FOLDER_RENAMED,FOLDER_OWNERSHIP_CHANGED,FOLDER_OPENED,FOLDER_MOVED,FOLDER_DELETED,FOLDER_CREATED,FOLDER_CLASSIFICATION_CHANGED,FOLDER_CENTRAL_ACCESS_POLICY_CHANGED,FOLDER_AUDITING_CHANGED,FOLDER_ATTRIBUTE_CHANGED,FOLDER_ACCESS_RIGHTS_CHANGED,FAILED_FOLDER_ACCESS,NETAPP_FOLDER_RENAMED,NETAPP_FOLDER_OWNERSHIP_CHANGED,NETAPP_FOLDER_MOVED,NETAPP_FOLDER_DELETED,NETAPP_FOLDER_CREATED,NETAPP_FOLDER_ACCESS_RIGHTS_CHANGED,EMC_FOLDER_RENAMED,EMC_FOLDER_OWNERSHIP_CHANGED,EMC_FLDER_MOVED,EMC_FOLDER_DELETED,EMC_FOLDER_CREATED,EMC_FOLDER_ACCESS_RIGHTS_CHANGED,FOLDER_RENAMED,FLUIDFS_FOLDER_OWNERSHIP_CHANGED,FLUIDFS_FOLDER_MOVED,FLUIDFS_FOLDER_DELETED,FLUIDFS_FOLDER_CREATED,FLUIDFS_FOLDER_AUDITING_CHANGED,FLUIDFS_FOLDER_ACCESS_RIGHTS_CHANGED");
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
