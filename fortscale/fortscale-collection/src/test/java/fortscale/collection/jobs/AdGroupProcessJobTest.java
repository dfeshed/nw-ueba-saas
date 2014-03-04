package fortscale.collection.jobs;

import java.beans.PropertyDescriptor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import fortscale.domain.ad.AdGroup;
import fortscale.utils.impala.ImpalaDateTime;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.IllegalStructuredProperty;
import fortscale.utils.properties.PropertiesResolver;
import fortscale.utils.properties.PropertyNotExistException;

public class AdGroupProcessJobTest {
	
	@Test
	public void outputFieldsTest() throws PropertyNotExistException, IllegalStructuredProperty{
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.ldapgroups.table.fields");
				
		HashMap<String, Class<?>> expectedFieldsMap = ImpalaParser.getTableFieldDefinitionMap(impalaTableFields);
		
		PropertyDescriptor propertyDescriptors[] = PropertyUtils.getPropertyDescriptors(AdGroup.class);
		Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<>();
		for(PropertyDescriptor propertyDescriptor: propertyDescriptors){
			propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
		}
		
		for(String fieldName: ImpalaParser.getTableFieldNames(impalaTableFields)){
			Assert.assertTrue(propertyDescriptorMap.containsKey(fieldName));

			Class<?> type = expectedFieldsMap.get(fieldName);
			Class<?> actualType = propertyDescriptorMap.get(fieldName).getPropertyType();
			if(type.equals(ImpalaDateTime.class)){
				type = Date.class;
			}
			Assert.assertEquals(type, actualType);
		}		
	}
}
