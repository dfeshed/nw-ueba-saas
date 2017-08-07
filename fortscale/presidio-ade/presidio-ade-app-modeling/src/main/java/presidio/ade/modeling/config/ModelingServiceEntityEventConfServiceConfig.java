package presidio.ade.modeling.config;

import fortscale.entity.event.EntityEventConfService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lior Govrin
 */
@Configuration
public class ModelingServiceEntityEventConfServiceConfig {
	@Bean
	public EntityEventConfService entityEventConfService() {
		return new EntityEventConfService();
	}
}
