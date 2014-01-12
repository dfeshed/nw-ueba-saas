package fortscale.collection.scoring.jobs;

import org.springframework.beans.factory.annotation.Autowired;

import fortscale.services.UserService;
import fortscale.services.fe.Classifier;
import fortscale.utils.logging.Logger;

public class SshScoringJob extends EventScoringJob{
	private static Logger logger = Logger.getLogger(SshScoringJob.class);
	
	@Autowired
	private UserService userService;
	
	protected void runSteps(String monitorId){
		boolean isSucceeded = runPig(monitorId);
		if(!isSucceeded){
			return;
		}
		
		isSucceeded = runUpdateUserWithSshScore(monitorId);
		if(!isSucceeded){
			return;
		}
	}
	
	private boolean runPig(String monitorId){
		String cmd = "/home/cloudera/fortscale/fortscale-scripts/scripts/uploadSSHDataToHDFS_part4_runpig.sh";
		logger.info("Running SSH pig with the following shell command: {}", cmd);
		String stepName = "Running SSH pig";
		
		
		Runtime run = Runtime.getRuntime();
		Process pr = null;			
		
		monitor.startStep(monitorId, stepName, 1);
		try {
			pr = run.exec(cmd);
			pr.waitFor();

		} catch (Exception e) {
			logger.error(String.format("while running the command %s, got the following exception", cmd), e);
			monitor.error(monitorId, stepName, String.format("while running the command %s, got the following exception %s", cmd, e.getMessage()));
			return false;
		}
		monitor.finishStep(monitorId, stepName);
		
		
		return true;
	}
	
	private boolean runUpdateUserWithSshScore(String monitorId){
		String stepName = "updateUserWithSshScore";
		monitor.startStep(monitorId, stepName, 2);
		try {
			userService.updateUserWithAuthScore(Classifier.ssh);
		} catch (Exception e) {
			logger.error("while running updateUserWithSshScore, got the following exception", e);
			monitor.error(monitorId, stepName, String.format("while running updateUserWithSshScore, got the following exception %s", e.getMessage()));
			return false;
		}
		monitor.finishStep(monitorId, stepName);
		
		return true;
	}	
}
