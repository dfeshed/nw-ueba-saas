package fortscale.collection.hadoop.pig;

import java.util.List;
import java.util.Properties;

import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.backend.executionengine.ExecJob;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.hadoop.pig.PigOperations;
import org.springframework.data.hadoop.pig.PigScript;
import org.springframework.stereotype.Component;

@Component("vpnPigRunner")
public class VpnPigRunner {
	
	@Autowired
	private PigOperations  pigOperations;
	@Value("${vpn.pig.script.location:file:resources/pig/vpn.pig}")
	private Resource vpnPigScriptResource;
	
	public void run(){
		DateTime dateTime = new DateTime();
		long runtime = dateTime.getMillis() / 1000;
		Long deltaTime = dateTime.minusDays(14).getMillis() / 1000;
		Properties scriptParameters = new Properties();
        scriptParameters.put("jarFilePath1", "/home/cloudera/fortscale/fs-paprika/event-bulk-scorer/bearded-pig/target/beardedpig-1.0-SNAPSHOT.jar");
        scriptParameters.put("jarFilePath2", "/home/cloudera/fortscale/fs-paprika/calibro/target/calibro-1.0-SNAPSHOT.jar");    
        scriptParameters.put("inputData", "/user/cloudera/data/vpn");
        scriptParameters.put("outputData", String.format("/user/cloudera/processeddata/vpn/runtime=%s",runtime));
        scriptParameters.put("deltaTime", deltaTime.toString());
        PigScript pigScript = new PigScript(vpnPigScriptResource, scriptParameters);
        List<ExecJob> execJobs = pigOperations.executeScript(pigScript);
        
        try {
			while(!execJobs.get(0).hasCompleted()){
				Thread.sleep(10000);
			}
		} catch (ExecException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
