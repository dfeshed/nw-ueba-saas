package fortscale.domain.sessionsplit.cache;

import fortscale.domain.sessionsplit.store.ISessionSplitStore;
import fortscale.domain.sessionsplit.store.SessionSplitStoreRedisConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SessionSplitStoreRedisConfiguration.class)
public class SessionSplitStoreCacheConfiguration {
    private final ISessionSplitStore sessionSplitStore;
    private final int maximumSize;

    @Autowired
    public SessionSplitStoreCacheConfiguration(
            ISessionSplitStore sessionSplitStore,
            @Value("${presidio.session.split.writer.maximum.size:10000}") int maximumSize) {

        this.sessionSplitStore = sessionSplitStore;
        this.maximumSize = maximumSize;
    }


    @Bean
    public SessionSplitStoreCacheImpl sessionSplitStoreCache() {
        return new SessionSplitStoreCacheImpl(
                sessionSplitStore,
                maximumSize);
    }
}
