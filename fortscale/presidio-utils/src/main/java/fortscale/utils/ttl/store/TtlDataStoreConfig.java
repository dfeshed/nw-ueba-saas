package fortscale.utils.ttl.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by maria_dorohin on 8/30/17.
 */
@Configuration
@Import({TtlDataRecordsStoreConfig.class})
public class TtlDataStoreConfig {
    @Autowired
    private TtlDataRecordsStore ttlDataRecordsStore;
    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public TtlDataStore ttlDataStore() {
        return new TtlDataStore(appName, ttlDataRecordsStore);
    }
}
