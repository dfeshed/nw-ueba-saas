package fortscale.ml.model.selector;

import fortscale.domain.core.dao.AlertsRepository;
import fortscale.domain.core.dao.MongoDbRepositoryUtil;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by baraks on 1/4/2017.
 */
@Configuration
@Import(MongodbTestConfig.class)
@EnableMongoRepositories(basePackageClasses = AlertsRepository.class,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes= AlertsRepository.class))
public class AlertTriggeringHighScoreContextSelectorTestConfig {
    @Bean
    public MongoDbRepositoryUtil mongoDbRepositoryUtil()
    {
        return new MongoDbRepositoryUtil();
    }
}
