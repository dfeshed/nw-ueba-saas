package fortscale.utils.ttl;

import fortscale.utils.ttl.store.TtlDataRepository;
//import fortscale.utils.ttl.store.TtlDataStoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.Duration;
import java.util.Collection;

/**
 * Created by maria_dorohin on 8/31/17.
 */
@Configuration
@EnableMongoRepositories(basePackages = "fortscale.utils.ttl.store")
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
    private TtlDataRepository ttlDataRepository;

    @Bean
    public TtlService ttlService(){
        return new TtlService(appName, ttlServiceAwares, defaultTtl, defaultCleanupInterval, ttlDataRepository);
    }
}
