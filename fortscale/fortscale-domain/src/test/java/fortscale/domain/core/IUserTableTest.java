package fortscale.domain.core;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;

import fortscale.utils.impala.ImpalaDateTime;
import fortscale.utils.impala.ImpalaParser;

public class IUserTableTest {
	@Test
	public void testFieldMapping() throws IOException{
		final Properties properties = new Properties();
		
		InputStream is = getClass().getResourceAsStream( "/META-INF/fortscale-config.properties" );

		properties.load(is);
		String impalaUserFields = properties.getProperty("impala.user.fields");
		
		PropertyDescriptor propertyDescriptors[] = PropertyUtils.getPropertyDescriptors(IUserTable.class);
		
		HashMap<String, Class<?>> expectedFieldsMap = ImpalaParser.getTableFieldDefinitionMap(impalaUserFields);

		Assert.assertEquals(expectedFieldsMap.size(), propertyDescriptors.length);
		for(PropertyDescriptor propertyDescriptor: propertyDescriptors){
			Assert.assertTrue(propertyDescriptor.getName() + " is not valid", expectedFieldsMap.containsKey(propertyDescriptor.getName()));
			Class<?> type = expectedFieldsMap.get(propertyDescriptor.getName());
			if(type.equals(ImpalaDateTime.class)){
				type = Long.class;
			}
			Class<?> actualType = propertyDescriptor.getPropertyType();
			Assert.assertEquals(propertyDescriptor.getName() + " is not of expected type", type, actualType);
		}
	}
	
}
