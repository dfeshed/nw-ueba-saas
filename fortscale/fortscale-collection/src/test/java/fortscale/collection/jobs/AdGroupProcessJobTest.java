package fortscale.collection.jobs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import fortscale.domain.ad.AdGroup;

public class AdGroupProcessJobTest {
	
	@Test
	public void outputFieldsTest() throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		final Properties properties = new Properties();
		String configPropertiesFile = "src/main/resources/META-INF/fortscale-collection.properties";
		InputStream fileStream = new FileInputStream(configPropertiesFile);

		properties.load(fileStream);
		String outputFields = properties.getProperty("collection.ad.group.output.fields");
		
		
		@SuppressWarnings("unchecked")
		Map<String,Object> desc = PropertyUtils.describe(new AdGroup());
		for(String outputField: outputFields.split(",")){
			Assert.assertTrue(desc.containsKey(outputField));
		}
	}
}
