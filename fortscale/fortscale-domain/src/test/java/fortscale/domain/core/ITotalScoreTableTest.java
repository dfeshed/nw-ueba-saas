package fortscale.domain.core;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.HashMap;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.IllegalStructuredProperty;
import fortscale.utils.properties.PropertiesResolver;
import fortscale.utils.properties.PropertyNotExistException;

public class ITotalScoreTableTest {

	@Test
	public void testFieldMapping() throws IOException, PropertyNotExistException, IllegalStructuredProperty{
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.total.scores.table.fields");
		
		PropertyDescriptor propertyDescriptors[] = PropertyUtils.getPropertyDescriptors(ITotalScore.class);
		
		HashMap<String, Class<?>> expectedFieldsMap = ImpalaParser.getTableFieldDefinitionMap(impalaTableFields);

		Assert.assertEquals(expectedFieldsMap.size(), propertyDescriptors.length-1);
		for(PropertyDescriptor propertyDescriptor: propertyDescriptors){
			if("runtime".equals(propertyDescriptor.getName())){
				continue;
			}
			Assert.assertTrue(expectedFieldsMap.containsKey(propertyDescriptor.getName()));
			Class<?> type = expectedFieldsMap.get(propertyDescriptor.getName());
			Class<?> actualType = propertyDescriptor.getPropertyType();
			Assert.assertEquals(type, actualType);
		}
	}
}
