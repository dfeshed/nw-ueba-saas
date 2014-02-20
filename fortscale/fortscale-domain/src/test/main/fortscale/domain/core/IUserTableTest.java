package fortscale.domain.core;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

public class IUserTableTest {

	
	@Test
	public void testFieldMapping() throws IOException{
		final Properties properties = new Properties();
		
		InputStream is = getClass().getResourceAsStream( "/META-INF/fortscale-config.properties" );

		properties.load(is);
		String impalaUserFields = properties.getProperty("impala.user.fields");
		
		PropertyDescriptor propertyDescriptors[] = PropertyUtils.getPropertyDescriptors(IUserTable.class);
		
		Set<String> expectedFieldsSet = new HashSet<>();
		expectedFieldsSet.addAll(Arrays.asList(impalaUserFields.split(",")));
		Assert.assertEquals(expectedFieldsSet.size(), propertyDescriptors.length);
		for(PropertyDescriptor propertyDescriptor: propertyDescriptors){
			Assert.assertTrue(expectedFieldsSet.contains(propertyDescriptor.getName()));
		}
	}
}
