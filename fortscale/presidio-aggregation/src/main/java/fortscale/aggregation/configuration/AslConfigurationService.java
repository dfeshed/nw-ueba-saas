package fortscale.aggregation.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * This service extends {@link AslConfigurationServiceBase}, but it is also aware of the {@link ApplicationContext}
 * and loads from it the base, overriding and additional configuration resources. The inheritors need to provide the
 * paths to these resources.
 *
 * @author Lior Govrin
 * @see AslConfigurationServiceBase
 */
public abstract class AslConfigurationService
		extends AslConfigurationServiceBase
		implements ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;

	/**
	 * @return the full path to the base configurations (file or folder)
	 */
	protected abstract String getBaseConfJsonFilesPath();

	/**
	 * @return the full path to the overriding configurations (file or folder)
	 */
	protected abstract String getBaseOverridingConfJsonFolderPath();

	/**
	 * @return the full path to the additional configurations (file or folder)
	 */
	protected abstract String getAdditionalConfJsonFolderPath();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		loadAslConfigurations();
	}

	@Override
	protected Resource[] getBaseConfigurationResources() throws IOException {
		return getConfigurationResources(getBaseConfJsonFilesPath());
	}

	@Override
	protected Resource[] getOverridingConfigurationResources() throws IOException {
		return getConfigurationResources(getBaseOverridingConfJsonFolderPath());
	}

	@Override
	protected Resource[] getAdditionalConfigurationResources() throws IOException {
		return getConfigurationResources(getAdditionalConfJsonFolderPath());
	}

	private Resource[] getConfigurationResources(String locationPattern) throws IOException {
		if (locationPattern == null) {
			return null;
		} else {
			return applicationContext.getResources(locationPattern);
		}
	}
}
