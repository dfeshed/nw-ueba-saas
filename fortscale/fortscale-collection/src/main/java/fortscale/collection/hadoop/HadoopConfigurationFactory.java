package fortscale.collection.hadoop;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.hadoop.configuration.ConfigurationFactoryBean;

public class HadoopConfigurationFactory extends ConfigurationFactoryBean implements ResourceLoaderAware{

	private ResourceLoader resourceLoader;
	private String resourcesString;
	
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	
	
	public void setResourcesString(String resourcesString) {
		this.resourcesString = resourcesString;
	}



	@Override
	public void afterPropertiesSet() throws Exception {
		if(!StringUtils.isEmpty(resourcesString)){
			Configuration internalConfig = createConfiguration(null);
			setConfiguration(internalConfig);
			for(String res: resourcesString.split(",")){
				internalConfig.addResource(resourceLoader.getResource(res).getURL());
			}
		}
		super.afterPropertiesSet();
	}
}
