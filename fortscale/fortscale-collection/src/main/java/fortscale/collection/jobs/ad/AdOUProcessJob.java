package fortscale.collection.jobs.ad;

import fortscale.collection.morphlines.RecordToBeanItemConverter;
import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.dao.AdOURepository;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.kitesdk.morphline.api.Record;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class AdOUProcessJob extends AdProcessJob {

	private static final Logger logger = Logger.getLogger(AdOUProcessJob.class);

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
		final AdOU existingOu = adOURepository.findByObjectGUID(adOU.getObjectGUID());
		if (existingOu != null) {
			logger.debug("Updating OU with objectGUID {}", existingOu.getObjectGUID());
			adOURepository.delete(existingOu);
		}

		adOURepository.save(adOU);
		adOU.setLastModified(new Date());
		return true;
	}

	@Override
	protected String getDataReceivedType() {
		return "OU";
	}
}