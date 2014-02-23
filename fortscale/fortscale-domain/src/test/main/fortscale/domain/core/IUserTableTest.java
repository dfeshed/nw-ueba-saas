package fortscale.domain.core;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import fortscale.utils.impala.ImpalaParser;

public class IUserTableTest {

	
	@Test
	public void testFieldMapping() throws IOException{
		final Properties properties = new Properties();
		
		InputStream is = getClass().getResourceAsStream( "/META-INF/fortscale-config.properties" );

		properties.load(is);
		String impalaUserFields = properties.getProperty("impala.user.fields");
		
		PropertyDescriptor propertyDescriptors[] = PropertyUtils.getPropertyDescriptors(IUserTable.class);
		
		HashMap<String, Class<?>> expectedFieldsMap = new HashMap<>();
		for(String fieldDef: impalaUserFields.split(",")){
			String fieldDefSplit[] = fieldDef.split(" ");
			Assert.assertFalse(expectedFieldsMap.containsKey(fieldDefSplit[0]));
			Class<?> type = ImpalaParser.convertImpalaTypeToJavaType(fieldDefSplit[1]);
			Assert.assertNotNull(type);
			expectedFieldsMap.put(fieldDefSplit[0], type);
		}
		Assert.assertEquals(expectedFieldsMap.size(), propertyDescriptors.length);
		for(PropertyDescriptor propertyDescriptor: propertyDescriptors){
			Assert.assertTrue(expectedFieldsMap.containsKey(propertyDescriptor.getName()));
			Class<?> type = expectedFieldsMap.get(propertyDescriptor.getName());
			Class<?> actualType = propertyDescriptor.getPropertyType();
			Assert.assertEquals(type, actualType);
		}
	}
	
}
