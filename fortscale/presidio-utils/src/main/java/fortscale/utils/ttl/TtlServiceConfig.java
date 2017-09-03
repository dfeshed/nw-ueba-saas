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
import java.util.Map;

/**
 * Created by maria_dorohin on 8/31/17.
 */
@Configuration
@Import({
        TtlDataStoreConfig.class,
})
public class TtlServiceConfig {

    @Autowired
    private Collection<TtlServiceAware> ttlServiceAwares;
    @Autowired
    private TtlDataStore ttlDataStore;
    @Value("${presidio.application.name}")
    private String appName;
    @Value("#{T(java.time.Duration).parse('${presidio.default.ttl.duration}')}")
    private Duration defaultTtl;
    @Value("#{T(java.time.Duration).parse('${presidio.default.cleanup.interval}')}")
    private Duration defaultCleanupInterval;

    @Bean
    public TtlService ttlService(){
        return new TtlService(appName, ttlServiceAwares, ttlDataStore, defaultTtl, defaultCleanupInterval);
    }
}
