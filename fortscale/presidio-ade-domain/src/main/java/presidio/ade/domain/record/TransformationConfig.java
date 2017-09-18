package presidio.ade.domain.record;

import fortscale.utils.recordreader.transformation.EpochtimeTransformation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 6/29/17.
 */
@Configuration
public class TransformationConfig {
    // TODO: Configure here all relevant transformations

    @Bean
    public EpochtimeTransformation epochtimeTransformation() {
        return new EpochtimeTransformation("two_minute_resolution_epochtime", "startInstant", 120);
    }
}
