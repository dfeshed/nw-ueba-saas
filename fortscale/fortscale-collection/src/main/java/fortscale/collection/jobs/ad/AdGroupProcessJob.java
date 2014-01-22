package fortscale.collection.jobs.ad;

import java.util.Date;

import org.kitesdk.morphline.api.Record;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.dao.AdGroupRepository;

public class AdGroupProcessJob extends AdProcessJob {
	
	@Autowired
	private AdGroupRepository adGroupRepository;
		
	private RecordToBeanItemConverter<AdGroup> converter;


	@Override
	protected void init(JobExecutionContext jobExecutionContext) throws JobExecutionException{
		super.init(jobExecutionContext);
		converter = new RecordToBeanItemConverter<>(getOutputFields());
	}

	@Override
	protected boolean isTimestampAlreadyProcessed(Date runtime) {
		return adGroupRepository.countByTimestampepoch(runtime.getTime()) > 0 ? true : false;
	}

	@Override
	protected void updateDb(Record record) throws Exception {
		AdGroup adGroup = new AdGroup();
		converter.convert(record, adGroup);
		adGroup.setLastModified(new Date());
		adGroupRepository.save(adGroup);
	}

}
