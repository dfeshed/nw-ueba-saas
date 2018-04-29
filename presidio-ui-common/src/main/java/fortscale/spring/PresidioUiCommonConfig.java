package fortscale.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath*:META-INF/spring/fortscale-common-entities-context.xml"})
public class PresidioUiCommonConfig {

}
