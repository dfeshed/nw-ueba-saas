package fortscale.collection.scoring.jobs;

import java.util.Date;

import org.apache.pig.backend.executionengine.ExecJob;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import fortscale.collection.hadoop.pig.VpnScoringPigRunner;
import fortscale.services.LogEventsEnum;
import fortscale.services.UserService;

public class VpnScoringJob extends EventScoringJob implements InitializingBean{
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private VpnScoringPigRunner vpnScoringPigRunner;
	
	@Value("${impala.vpn.table.name:}")
	private String tableName;
	
	@Override
	protected void runSteps() throws Exception{
		runScoringSteps(LogEventsEnum.vpn);		
	}
	
	@Override
	protected boolean runUpdateUserWithEventScore(Date runtime){
		userService.updateUserWithVpnScore(runtime);
		
		return true;
	}

	@Override
	protected String getTableName() {
		return tableName;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(tableName);
		
	}

	@Override
	protected ExecJob runPig(Long runtime, Long deltaTime) throws Exception {
		return vpnScoringPigRunner.run(runtime, deltaTime);
	}
	
	@Override
	protected int getTotalNumOfSteps() {
		return 4;
	}
}
