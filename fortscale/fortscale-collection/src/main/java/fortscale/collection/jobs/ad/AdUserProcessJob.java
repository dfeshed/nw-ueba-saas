package fortscale.collection.jobs.ad;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.kitesdk.morphline.api.Record;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.morphlines.RecordToBeanItemConverter;
import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.services.AdministratorAccountService;
import fortscale.services.ExecutiveAccountService;
import fortscale.services.UserServiceFacade;
import fortscale.services.impl.UsernameService;

public class AdUserProcessJob extends AdProcessJob {

	@Autowired
	private AdUserRepository adUserRepository;
	
	@Autowired
	private UserServiceFacade userServiceFacade;
	
	@Autowired
	private UsernameService usernameService;
	
	@Autowired
	private AdministratorAccountService administratorAccountService;
	
	@Autowired
	private ExecutiveAccountService executiveAccountService;

	private RecordToBeanItemConverter<AdUser> converter;
	
	@Override
	protected void init(JobExecutionContext jobExecutionContext) throws JobExecutionException{
		super.init(jobExecutionContext);
		converter = new RecordToBeanItemConverter<>(getOutputFields());
	}
	
	@Override
	protected int getTotalNumOfSteps() {
		return 5;
	}

	@Override
	protected boolean isTimestampAlreadyProcessed(Date runtime) {
		return adUserRepository.countByTimestampepoch(runtime.getTime()) > 0 ? true : false;
	}

	@Override
	protected boolean updateDb(Record record) throws Exception {
		AdUser adUser = new AdUser();
		converter.convert(record, adUser);
		if(StringUtils.isEmpty(adUser.getDistinguishedName()) || StringUtils.isEmpty(adUser.getObjectGUID())){
			return false;
		}
		adUser.setLastModified(new Date());
		adUserRepository.save(adUser);
		userServiceFacade.updateUserWithADInfo(adUser);
		
		return true;
	}
	
	@Override
	protected String getDataRecievedType() {
		return "Users";
	}
	
	protected void runFinalStep(){
		startNewStep("update username set");
		usernameService.update();
		// Update admin tag
		executiveAccountService.update();
		administratorAccountService.update();
		
		finishStep();
	}
}
