package fortscale.collection.jobs.ad;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.kitesdk.morphline.api.Record;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.morphlines.RecordToBeanItemConverter;
import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.dao.AdOURepository;

public class AdOUProcessJob extends AdProcessJob {
	
	@Autowired
	private AdOURepository adOURepository;
		
	private RecordToBeanItemConverter<AdOU> converter;


	@Override
	protected void init(JobExecutionContext jobExecutionContext) throws JobExecutionException{
		super.init(jobExecutionContext);
		converter = new RecordToBeanItemConverter<>("AdOUProcessJob", this.statsService, getOutputFields());
	}

	@Override
	protected boolean isTimestampAlreadyProcessed(Date runtime) {
		return adOURepository.countByTimestampepoch(runtime.getTime()) > 0 ? true : false;
	}

	@Override
	protected boolean updateDb(Record record) throws Exception {
		AdOU adOU = new AdOU();
		converter.convert(record, adOU);
		if(StringUtils.isEmpty(adOU.getDistinguishedName()) || StringUtils.isEmpty(adOU.getObjectGUID())){
			return false;
		}
		adOU.setLastModified(new Date());
		adOURepository.save(adOU);
		
		return true;
	}

	@Override
	protected String getDataRecievedType() {
		return "OU";
	}
}