package fortscale.collection.shellscripts;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import fortscale.utils.test.category.LinuxTestCategory;


@Category(LinuxTestCategory.class)
public class CollectionShellScriptsTestInt {

	@Ignore("breaks build in team city as the scripts repository was not fetched")
	@Test
	public void testGetDCsRunWithNoExceptions() throws IOException{
		final Properties properties = new Properties();
		
		InputStream is = getClass().getResourceAsStream( "/META-INF/fortscale-config.properties" );

		properties.load(is);
		String fortscaleHomeDir = properties.getProperty("fortscale.home.dir");
		String getDCsScriptPath = String.format("%s/fortscale-scripts/scripts/getDCs.sh", fortscaleHomeDir);
		
		List<String> dcs = new ArrayList<>();
		String commands[] = {getDCsScriptPath, "short"};
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		Process pr = processBuilder.start();
		
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			 
			String line = null;
			while ((line = reader.readLine()) != null) {
				if(!StringUtils.isEmpty(line)){
					dcs.add(line);
				}
			}
		} finally{
			if(reader != null){
				reader.close();
			}
		}
		
		assertTrue(!dcs.isEmpty());
	}
}
