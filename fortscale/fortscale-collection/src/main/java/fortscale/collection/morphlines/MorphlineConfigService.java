package fortscale.collection.morphlines;

import com.typesafe.config.Config;
import fortscale.collection.configuration.CollectionPropertiesResolver;
import fortscale.utils.properties.IllegalStructuredProperty;
import fortscale.utils.properties.PropertyNotExistException;
import org.kitesdk.morphline.base.Configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MorphlineConfigService {

	@Autowired
	CollectionPropertiesResolver propertiesResolver;


	public String getStringValue(Configs configs, Config config,String path) throws PropertyNotExistException, IllegalStructuredProperty{
		String value = configs.getString(config, path);

		return propertiesResolver.getEnvPropertyValue(value);
	}
}
