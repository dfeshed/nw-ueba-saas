package fortscale.domain.fe.dao;

import java.beans.PropertyDescriptor;
import java.util.Date;
import java.util.HashMap;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import fortscale.domain.fe.VpnScore;
import fortscale.utils.impala.ImpalaDateTime;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.IllegalStructuredProperty;
import fortscale.utils.properties.PropertiesResolver;
import fortscale.utils.properties.PropertyNotExistException;

public class VpnScoreTest {
	@Test
	public void fieldsTest() throws PropertyNotExistException, IllegalStructuredProperty{
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.score.vpn.table.fields");
				
		HashMap<String, Class<?>> expectedFieldsMap = ImpalaParser.getTableFieldDefinitionMap(impalaTableFields);
		
		PropertyDescriptor propertyDescriptors[] = PropertyUtils.getPropertyDescriptors(VpnScore.class);
		
		for(PropertyDescriptor propertyDescriptor: propertyDescriptors){
			String fieldName = propertyDescriptor.getName();
			if(fieldName.equals("class")){
				continue;
			}
			Assert.assertTrue(expectedFieldsMap.containsKey(fieldName));

			Class<?> type = expectedFieldsMap.get(fieldName);
			Class<?> actualType = propertyDescriptor.getPropertyType();
			if(type.equals(ImpalaDateTime.class)){
				type = Date.class;
			}
			Assert.assertEquals(type, actualType);
		}
		
	}
}
