package presidio.ade.sdk.ttl;

import fortscale.utils.ttl.StoreManager;
import fortscale.utils.ttl.StoreManagerAware;
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
public class StoreManagerConfig {

    @Value("${spring.application.name}")
    private String appName;
    @Autowired
    private Collection<StoreManagerAware> storeManagerAwares;
    @Autowired
    private TtlDataRepository ttlDataRepository;
    @Value("#{T(java.time.Duration).parse('${presidio.default.ttl.duration:P60D}')}")
    private Duration defaultTtl;
    @Value("#{T(java.time.Duration).parse('${presidio.default.cleanup.interval:P60D}')}")
    private Duration defaultCleanupInterval;
    @Value("${presidio.execute.ttl.cleanup:true}")
    private Boolean executeTtlCleanup;


    @Bean
    public StoreManager storeManager(){
        return new StoreManager(appName, storeManagerAwares, defaultTtl, defaultCleanupInterval, ttlDataRepository, executeTtlCleanup);
    }
}
