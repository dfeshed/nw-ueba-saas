package presidio.utils.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath*:META-INF/spring/fortscale-common-context.xml","classpath*:META-INF/spring/fortscale-logging-context.xml"})
public class PresidioUiUtilsConfiguration {
}
