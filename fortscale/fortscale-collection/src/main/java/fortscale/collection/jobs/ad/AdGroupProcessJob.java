package fortscale.collection.jobs.ad;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.kitesdk.morphline.api.Record;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.collection.morphlines.RecordToBeanItemConverter;
import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.dao.AdGroupRepository;

public class AdGroupProcessJob extends AdProcessJob {
	
	@Autowired
	private AdGroupRepository adGroupRepository;
		
	private RecordToBeanItemConverter<AdGroup> converter;


	@Override
	protected void init(JobExecutionContext jobExecutionContext) throws JobExecutionException{
		super.init(jobExecutionContext);
		converter = new RecordToBeanItemConverter<>("AdGroupProcessJob", this.statsService, getOutputFields());
	}

	@Override
	protected boolean isTimestampAlreadyProcessed(Date runtime) {
		return adGroupRepository.countByTimestampepoch(runtime.getTime()) > 0 ? true : false;
	}

	@Override
	protected boolean updateDb(Record record) throws Exception {
		AdGroup adGroup = new AdGroup();
		converter.convert(record, adGroup);
		if(StringUtils.isEmpty(adGroup.getDistinguishedName()) || StringUtils.isEmpty(adGroup.getObjectGUID())){
			return false;
		}
		adGroup.setLastModified(new Date());
		adGroupRepository.save(adGroup);
		
		return true;
	}
	
	@Override
	protected String getDataRecievedType() {
		return "Groups";
	}

}
