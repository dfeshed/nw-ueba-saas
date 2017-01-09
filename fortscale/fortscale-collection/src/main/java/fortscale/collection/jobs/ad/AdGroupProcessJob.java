package fortscale.collection.jobs.ad;

import fortscale.collection.morphlines.RecordToBeanItemConverter;
import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.dao.AdGroupRepository;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.kitesdk.morphline.api.Record;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class AdGroupProcessJob extends AdProcessJob {

	private static final Logger logger = Logger.getLogger(AdGroupProcessJob.class);
	
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
		return adGroupRepository.countByTimestampepoch(runtime.getTime(), AdGroup.COLLECTION_NAME) > 0 ? true : false;
	}

	@Override
	protected boolean updateDb(Record record) throws Exception {
		AdGroup adGroup = new AdGroup();
		converter.convert(record, adGroup);
		if(StringUtils.isEmpty(adGroup.getDistinguishedName()) || StringUtils.isEmpty(adGroup.getObjectGUID())){
			return false;
		}
		final AdGroup existingGroup = adGroupRepository.findByObjectGUID(adGroup.getObjectGUID());
		if (existingGroup != null) {
			logger.info("Updating group with objectGUID {}", existingGroup.getObjectGUID());
			adGroupRepository.delete(existingGroup);
		}

		adGroup.setLastModified(new Date());
		adGroupRepository.save(adGroup);
		
		return true;
	}
	
	@Override
	protected String getDataReceivedType() {
		return "Groups";
	}

}
