package fortscale.collection.hadoop.pig;

import java.util.List;
import java.util.Properties;

import org.apache.pig.PigServer;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.backend.executionengine.ExecJob;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.hadoop.pig.PigOperations;
import org.springframework.data.hadoop.pig.PigScript;
import org.springframework.data.hadoop.pig.PigServerFactory;
import org.springframework.stereotype.Component;

import fortscale.utils.logging.Logger;

@Component
public class PigRunner implements InitializingBean {
	private static Logger logger = Logger.getLogger(PigRunner.class);
	
	@Autowired
	private PigOperations  pigOperations;
	
	@Autowired
	private PigServerFactory pigFactory;
	
	@Value("file:${collection.lib.dir}/beardedpig-1.0-SNAPSHOT.jar")
	private Resource beardedpigJar;
	@Value("file:${collection.lib.dir}/calibro-1.0-SNAPSHOT.jar")
	private Resource calibroJar;
	
	
	public ExecJob run(Resource pigScriptResource, Properties scriptParameters) throws NoPigJobExecutedException, ExecException, InterruptedException{
        PigScript pigScript = new PigScript(pigScriptResource, scriptParameters);
        List<ExecJob> execJobs = pigOperations.executeScript(pigScript);
        
        if(execJobs.isEmpty()){
        	throw new NoPigJobExecutedException("execJobs is empty.");
        }
        
        if(execJobs.size() > 1){
        	logger.warn("got more then one exec jobs. expected to get only one.");
        }
        
        ExecJob execJob = execJobs.get(0);
        while(!execJob.hasCompleted()){
			Thread.sleep(10000);
		}
        
        return execJob;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		// register pig udf jars here instead of using the REGISTER command 
		// in the pig scripts. It is assumed this method is run only once since
		// the PigRunner class is singleton
		PigServer server = pigFactory.getPigServer();
		server.registerJar(beardedpigJar.getFile().getAbsolutePath());
		server.registerJar(calibroJar.getFile().getAbsolutePath());
	}
	
}
