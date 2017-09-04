package presidio.ade.sdk.common;

import fortscale.utils.ttl.TtlService;
import fortscale.utils.ttl.TtlServiceAware;
import fortscale.utils.ttl.store.TtlDataStore;
import fortscale.utils.ttl.store.TtlDataStoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
        TtlDataStoreConfig.class,
})
public class TtlServiceSdkConfig {

    @Autowired
    private Collection<TtlServiceAware> ttlServiceAwares;
    @Autowired
    private TtlDataStore ttlDataStore;
    @Value("#{T(java.time.Duration).parse('${presidio.default.ttl.duration:P60D}')}")
    private Duration defaultTtl;
    @Value("#{T(java.time.Duration).parse('${presidio.default.cleanup.interval:P60D}')}")
    private Duration defaultCleanupInterval;

    @Autowired
    ApplicationContext ctx;

    @Bean
    public TtlService ttlService(){
        return new TtlService(ttlServiceAwares, ttlDataStore, defaultTtl, defaultCleanupInterval);
    }
}
