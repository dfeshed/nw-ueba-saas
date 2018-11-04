package presidio.ade.domain.record;

import fortscale.utils.recordreader.transformation.EpochtimeTransformation;
import fortscale.utils.recordreader.transformation.JoinerTransformation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

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

    @Bean
    public JoinerTransformation processFilePathTransformation(){
        return new JoinerTransformation("processFilePath", Arrays.asList("processDirectory", "processFileName"), "\\");
    }

    @Bean
    public JoinerTransformation srcProcessFilePathTransformation() {
        return new JoinerTransformation("srcProcessFilePath", Arrays.asList("srcProcessDirectory", "srcProcessFileName"), "\\");
    }

    @Bean
    public JoinerTransformation dstProcessFilePathTransformation() {
        return new JoinerTransformation("dstProcessFilePath", Arrays.asList("dstProcessDirectory", "dstProcessFileName"), "\\");
    }
}
