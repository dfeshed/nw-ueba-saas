package fortscale.domain.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import fortscale.utils.impala.ImpalaParser;

public class SecurityEvents4769DataTableTest {

	@Test
	public void testFieldMapping() throws IOException{
		final Properties properties = new Properties();
		
		InputStream is = getClass().getResourceAsStream( "/META-INF/fortscale-config.properties" );

		properties.load(is);
		String impalaTableFields = properties.getProperty("impala.data.security.events.4769.table.fields");
				
		HashMap<String, Class<?>> expectedFieldsMap = new HashMap<>();
		for(String fieldDef: impalaTableFields.split(",")){
			String fieldDefSplit[] = fieldDef.split(" ");
			Assert.assertFalse(expectedFieldsMap.containsKey(fieldDefSplit[0]));
			Class<?> type = ImpalaParser.convertImpalaTypeToJavaType(fieldDefSplit[1]);
			Assert.assertNotNull(type);
			expectedFieldsMap.put(fieldDefSplit[0], type);
		}
	}
}
