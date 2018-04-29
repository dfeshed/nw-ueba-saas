package fortscale.domain.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath*:META-INF/spring/fortscale-domain-context.xml"})
public class PresidioUiDomainConfiguration {
}
