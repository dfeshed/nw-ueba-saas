package presidio.utils.spring;

import fortscale.utils.image.ImageUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PresidioUiUtilsConfiguration {
    @Bean(name = "imageUtils")
    ImageUtils imageUtils(){
        return new ImageUtils();
    }


}
