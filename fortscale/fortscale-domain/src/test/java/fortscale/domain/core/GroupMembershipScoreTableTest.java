package fortscale.domain.core;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.HashMap;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import fortscale.domain.fe.IGroupMembershipScore;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.IllegalStructuredProperty;
import fortscale.utils.properties.PropertiesResolver;
import fortscale.utils.properties.PropertyNotExistException;

public class GroupMembershipScoreTableTest {

	@Test
	public void testFieldMapping() throws IOException, PropertyNotExistException, IllegalStructuredProperty{
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.ldap.group.membership.scores.table.fields");
		String impalaGroupMembershipScoringTablePartitionType = propertiesResolver.getProperty("impala.ldap.group.membership.scores.table.partition.type");
		PartitionStrategy partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaGroupMembershipScoringTablePartitionType);
		PropertyDescriptor propertyDescriptors[] = PropertyUtils.getPropertyDescriptors(IGroupMembershipScore.class);
		
		HashMap<String, Class<?>> expectedFieldsMap = ImpalaParser.getTableFieldDefinitionMap(impalaTableFields);

		Assert.assertEquals(expectedFieldsMap.size(), propertyDescriptors.length-1);
		for(PropertyDescriptor propertyDescriptor: propertyDescriptors){
			if(propertyDescriptor.getName().equals(partitionStrategy.getImpalaPartitionFieldName())){
				continue;
			}
			Assert.assertTrue(expectedFieldsMap.containsKey(propertyDescriptor.getName()));
			Class<?> type = expectedFieldsMap.get(propertyDescriptor.getName());
			Class<?> actualType = propertyDescriptor.getPropertyType();
			Assert.assertEquals(type, actualType);
		}
	}
}
