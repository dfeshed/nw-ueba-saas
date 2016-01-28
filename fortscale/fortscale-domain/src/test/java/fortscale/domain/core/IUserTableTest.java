package fortscale.domain.core;


import java.io.IOException;
import java.util.HashMap;

import fortscale.utils.properties.IllegalStructuredProperty;
import fortscale.utils.properties.PropertiesResolver;
import fortscale.utils.properties.PropertyNotExistException;
import org.junit.Assert;
import org.junit.Test;

import fortscale.utils.impala.ImpalaParser;

public class IUserTableTest {
	@Test
	public void testFieldMapping() throws IOException, PropertyNotExistException, IllegalStructuredProperty {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.user.fields");

		HashMap<String, Class<?>> expectedFieldsMap = ImpalaParser.getTableFieldDefinitionMap(impalaTableFields);
		Assert.assertEquals(ImpalaParser.getTableFieldNames(impalaTableFields).size(), expectedFieldsMap.size());
		}
	}

