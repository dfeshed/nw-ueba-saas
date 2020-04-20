package fortscale.aggregation.configuration;

import fortscale.utils.logging.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * The {@link AslResourceFactory} is aware of the {@link ApplicationContext} and gets from it {@link Resource}s
 * according to an input location pattern. If the location pattern cannot be resolved into resources, the factory
 * returns null.
 *
 * @author Lior Govrin
 */
public class AslResourceFactory implements ApplicationContextAware {
	private static final Logger logger = Logger.getLogger(AslResourceFactory.class);

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public Resource[] getResources(String locationPattern) {
		if (locationPattern == null) {
			logger.info("Location pattern is null - Returning null.");
			return null;
		}

		try {
			return applicationContext.getResources(locationPattern);
		} catch (IOException e) {
			logger.info("Cannot resolve {} into resources - Returning null.", locationPattern, e);
			return null;
		}
	}
}
