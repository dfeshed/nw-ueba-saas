package fortscale.collection.jobs.ad;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.kitesdk.morphline.api.Record;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.morphlines.RecordToBeanItemConverter;
import fortscale.domain.ad.AdComputer;
import fortscale.domain.ad.dao.AdComputerRepository;
import fortscale.services.ComputerService;

public class AdComputerProcessJob extends AdProcessJob {
	
	@Autowired
	private AdComputerRepository adComputerRepository;
		
	@Autowired
	private ComputerService service;
	
	private RecordToBeanItemConverter<AdComputer> converter;


	@Override
	protected void init(JobExecutionContext jobExecutionContext) throws JobExecutionException{
		super.init(jobExecutionContext);
		converter = new RecordToBeanItemConverter<>("AdComputerProcessJob", this.statsService, getOutputFields());
	}

	@Override
	protected boolean isTimestampAlreadyProcessed(Date runtime) {
		return adComputerRepository.countByTimestampepoch(runtime.getTime()) > 0 ? true : false;
	}

	@Override
	protected boolean updateDb(Record record) throws Exception {
		AdComputer adComputer = new AdComputer();
		converter.convert(record, adComputer);
		if(StringUtils.isEmpty(adComputer.getDistinguishedName()) || StringUtils.isEmpty(adComputer.getObjectGUID())){
			return false;
		}
		adComputer.setLastModified(new Date());
		adComputerRepository.save(adComputer);
		service.updateComputerWithADInfo(adComputer);
		
		return true;
	}

	@Override
	protected String getDataRecievedType() {
		return "Computers";
	}
}