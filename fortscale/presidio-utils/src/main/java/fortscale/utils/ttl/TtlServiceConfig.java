package fortscale.utils.ttl;

import fortscale.utils.ttl.store.TtlDataStore;
import fortscale.utils.ttl.store.TtlDataStoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.util.Collection;

/**
 * Created by maria_dorohin on 8/31/17.
 */
@Configuration
@Import({
        TtlDataStoreConfig.class
})
public class TtlServiceConfig {

    @Value("${spring.application.name}")
    private String appName;
    @Autowired
    private Collection<TtlServiceAware> ttlServiceAwares;
    @Value("#{T(java.time.Duration).parse('${presidio.default.ttl.duration}')}")
    private Duration defaultTtl;
    @Value("#{T(java.time.Duration).parse('${presidio.default.cleanup.interval}')}")
    private Duration defaultCleanupInterval;
    @Autowired
    private TtlDataStore ttlDataStore;

    @Bean
    public TtlService ttlService(){
        return new TtlService(appName, ttlServiceAwares, defaultTtl, defaultCleanupInterval, ttlDataStore);
    }
}
