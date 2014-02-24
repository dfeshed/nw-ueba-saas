package fortscale.domain.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import fortscale.utils.impala.ImpalaParser;

public class SshDataTableTest {
	@Test
	public void testFieldMapping() throws IOException{
		final Properties properties = new Properties();
		
		InputStream is = getClass().getResourceAsStream( "/META-INF/fortscale-config.properties" );

		properties.load(is);
		String impalaTableFields = properties.getProperty("impala.data.ssh.table.fields");
				
		HashMap<String, Class<?>> expectedFieldsMap = ImpalaParser.getTableFieldDefinitionMap(impalaTableFields);
		Assert.assertEquals(ImpalaParser.getTableFieldNames(impalaTableFields).size(), expectedFieldsMap.size());
	}
}
