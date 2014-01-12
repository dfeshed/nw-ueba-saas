package fortscale.collection.scoring.jobs;

import org.springframework.beans.factory.annotation.Autowired;

import fortscale.services.UserService;
import fortscale.utils.logging.Logger;

public class VpnScoringJob extends EventScoringJob{
	private static Logger logger = Logger.getLogger(VpnScoringJob.class);
	
	@Autowired
	private UserService userService;
	
	protected void runSteps(String monitorId){
		boolean isSucceeded = runPig(monitorId);
		if(!isSucceeded){
			return;
		}
		
		isSucceeded = runUpdateUserWithVpnScore(monitorId);
		if(!isSucceeded){
			return;
		}
	}
	
	private boolean runPig(String monitorId){
		String cmd = "/home/cloudera/fortscale/fortscale-scripts/scripts/uploadVPNDataToHDFS_part4_runEBS.sh";
		logger.info("Running VPN pig with the following shell command: {}", cmd);
		String stepName = "Running VPN pig";
		
		
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
	
	private boolean runUpdateUserWithVpnScore(String monitorId){
		String stepName = "updateUserWithVpnScore";
		monitor.startStep(monitorId, stepName, 2);
		try {
			userService.updateUserWithVpnScore();
		} catch (Exception e) {
			logger.error("while running updateUserWithVpnScore, got the following exception", e);
			monitor.error(monitorId, stepName, String.format("while running updateUserWithVpnScore, got the following exception %s", e.getMessage()));
			return false;
		}
		monitor.finishStep(monitorId, stepName);
		
		return true;
	}	
}
