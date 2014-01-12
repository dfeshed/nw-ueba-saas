package fortscale.collection.scoring.jobs;

import org.springframework.beans.factory.annotation.Autowired;

import fortscale.services.UserService;
import fortscale.services.fe.Classifier;
import fortscale.utils.logging.Logger;

public class LoginScoringJob extends EventScoringJob {
	private static Logger logger = Logger.getLogger(LoginScoringJob.class);
	
	@Autowired
	private UserService userService;
	
	protected void runSteps(String monitorId){
		boolean isSucceeded = runPrepareRegex(monitorId);
		if(!isSucceeded){
			return;
		}
		
		isSucceeded = runPig(monitorId);
		if(!isSucceeded){
			return;
		}
		
		isSucceeded = runUpdateUserWithLoginScore(monitorId);
		if(!isSucceeded){
			return;
		}
	}
	
	private boolean runPrepareRegex(String monitorId){
		String cmd = "/home/cloudera/fortscale/fortscale-scripts/scripts/uploadWMIDataToHDFS_part4_prepareregex.sh";
		String stepName = "prepareregex";
		
		return runCmd(monitorId, cmd, stepName);
	}
	
	private boolean runPig(String monitorId){
		String cmd = "/home/cloudera/fortscale/fortscale-scripts/scripts/uploadWMIDataToHDFS_part5_runpig.sh";
		String stepName = "LOGIN pig";
		
		return runCmd(monitorId, cmd, stepName);
	}
	
	private boolean runUpdateUserWithLoginScore(String monitorId){
		String stepName = "updateUserWithLoginScore";
		monitor.startStep(monitorId, stepName, 2);
		try {
			userService.updateUserWithAuthScore(Classifier.auth);
		} catch (Exception e) {
			logger.error("while running updateUserWithLoginScore, got the following exception", e);
			monitor.error(monitorId, stepName, String.format("while running updateUserWithLoginScore, got the following exception %s", e.getMessage()));
			return false;
		}
		monitor.finishStep(monitorId, stepName);
		
		return true;
	}	
}
