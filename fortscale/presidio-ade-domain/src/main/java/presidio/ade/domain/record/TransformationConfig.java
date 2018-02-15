package presidio.ade.domain.record;

import fortscale.utils.recordreader.transformation.EpochtimeTransformation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Barak Schuster
 * @author Lior Govrin
 */
@Configuration
public class TransformationConfig {
    /***********************************************
     * Configure here all relevant transformations *
     ***********************************************/

    @Bean
    public EpochtimeTransformation twoMinuteResolutionEpochtimeTransformation() {
        return new EpochtimeTransformation("two_minute_resolution_epochtime", "startInstant", 120);
    }

    @Bean
    public EpochtimeTransformation oneHourResolutionEpochtimeTransformation() {
        return new EpochtimeTransformation("one_hour_resolution_epochtime", "startInstant", 3600);
    }
}
