package fortscale.spring;

import fortscale.controllers.AlertsController;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Created by shays on 21/05/2017.
 */
@Import(MongoConfig.class)
public class WebConf {

    @Bean
    AlertsController getAlertsController(){
        return new AlertsController();
    }
}
