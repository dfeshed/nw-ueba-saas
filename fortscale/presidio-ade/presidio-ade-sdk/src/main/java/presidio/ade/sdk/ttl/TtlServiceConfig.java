package presidio.ade.sdk.ttl;

import fortscale.utils.ttl.TtlService;
import fortscale.utils.ttl.TtlServiceAware;
import fortscale.utils.ttl.store.TtlDataRepository;
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
@EnableMongoRepositories(basePackageClasses = fortscale.utils.ttl.store.TtlDataRepository.class)
public class TtlServiceConfig {

    @Value("${spring.application.name}")
    private String appName;
    @Autowired
    private Collection<TtlServiceAware> ttlServiceAwares;
    @Autowired
    private TtlDataRepository ttlDataRepository;
    @Value("#{T(java.time.Duration).parse('${presidio.default.ttl.duration:P60D}')}")
    private Duration defaultTtl;
    @Value("#{T(java.time.Duration).parse('${presidio.default.cleanup.interval:P60D}')}")
    private Duration defaultCleanupInterval;
    @Value("${presidio.execute.cleanup:true}")
    private Boolean executeCleanup;


    @Bean
    public TtlService ttlService(){
        return new TtlService(appName, ttlServiceAwares, defaultTtl, defaultCleanupInterval, ttlDataRepository, executeCleanup);
    }
}
