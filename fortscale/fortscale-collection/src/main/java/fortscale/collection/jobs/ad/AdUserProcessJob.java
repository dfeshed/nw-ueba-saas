package fortscale.collection.jobs.ad;

import java.util.Date;

import org.kitesdk.morphline.api.Record;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.services.UserService;

public class AdUserProcessJob extends AdProcessJob {

	@Autowired
	private AdUserRepository adUserRepository;
	
	@Autowired
	private UserService userService;
	
	private RecordToBeanItemConverter<AdUser> converter;
	
	@Override
	protected void init(JobExecutionContext jobExecutionContext) throws JobExecutionException{
		super.init(jobExecutionContext);
		converter = new RecordToBeanItemConverter<>(getOutputFields());
	}

	@Override
	protected boolean isTimestampAlreadyProcessed(Date runtime) {
		return adUserRepository.countByTimestampepoch(runtime.getTime()) > 0 ? true : false;
	}

	@Override
	protected void updateDb(Record record) throws Exception {
		AdUser adUser = new AdUser();
		converter.convert(record, adUser);
		adUser.setLastModified(new Date());
		adUserRepository.save(adUser);
		userService.updateUserWithADInfo(adUser);
	}
	
	
}
