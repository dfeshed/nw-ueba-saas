package fortscale.ml.model.selector;

import fortscale.domain.core.dao.MongoDbRepositoryUtil;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Created by baraks on 1/4/2017.
 */
@Configuration
@Profile("test")
@Import(MongodbTestConfig.class)

public class AlertTriggeringHighScoreContextSelectorTestConfig {
    @Bean
    public MongoDbRepositoryUtil mongoDbRepositoryUtil()
    {
        return new MongoDbRepositoryUtil();
    }
}
