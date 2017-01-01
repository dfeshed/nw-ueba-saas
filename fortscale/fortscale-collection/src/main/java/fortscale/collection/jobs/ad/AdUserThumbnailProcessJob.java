package fortscale.collection.jobs.ad;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.ad.AdUserThumbnail;
import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;
import fortscale.domain.ad.dao.AdUserThumbnailService;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.services.ActiveDirectoryService;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@DisallowConcurrentExecution
public class AdUserThumbnailProcessJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(AdUserThumbnailProcessJob.class);
		
	@Autowired
	private AdUserThumbnailService adUserThumbnailservice;

	@Autowired
	private ActiveDirectoryService activeDirectoryService;

	@Value("${users.ou.filter:}")
    private String ouUsersFilter;

	private String ldapFieldSeparator;
	
	private int adUserThumbnailBufferSize;

	private String filter;

	private String adFields;
	
	private List<AdUserThumbnail> adUserThumbnails = new ArrayList<>();
	
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map

		ldapFieldSeparator = jobDataMapExtension.getJobDataMapStringValue(map, "ldapFieldSeparator");
		adUserThumbnailBufferSize = jobDataMapExtension.getJobDataMapIntValue(map, "adUserThumbnailBufferSize");

		//AD search filter
		filter = jobDataMapExtension.getJobDataMapStringValue(map, "filter");
		//AD selected fields
		adFields = jobDataMapExtension.getJobDataMapStringValue(map, "adFields");
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 1;
	}

	@Override
	protected void runSteps() throws Exception {
		startNewStep("Fetch Thumbnail from AD");

		getFromActiveDirectory(filter, adFields, -1);
		flushAdUserThumbnailBuffer();

		finishStep();
	}



	/**
	 * This is the main method for the job. It connects to all of the domains by iterating
	 * over each one of them and attempting to connect to their DCs until one such connection is successful.
	 * It then performs the requested search according to the filter and saves the results using the {@code fileWriter}.
	 *
	 * @param  filter		   The Active Directory search filter (which object class is required)
	 * @param  adFields	   	   The Active Directory attributes to return in the search
	 * @param  resultLimit	   A limit on the search results (mostly for testing purposes) should be <= 0 for no limit
	 */
	private void getFromActiveDirectory(String filter, String adFields, int resultLimit) throws Exception {
		try {
			activeDirectoryService.getFromActiveDirectory(filter, adFields, resultLimit, new AdUserThumbnailProcessJobHandler());
		} catch (Exception e) {
			final String errorMessage = this.getClass().getSimpleName() + " failed. Failed to fetch from Active Directory";
			logger.error(errorMessage);
			throw new JobExecutionException(errorMessage, e);
		}
	}

	private void processLine(String line){
		String lineSplit[] = StringUtils.split(line, ldapFieldSeparator);
		if(lineSplit.length != 2){
			return;
		}

		final String objectGUID = lineSplit[1];
		AdUserThumbnail adUserThumbnail = new AdUserThumbnail();
		adUserThumbnail.setObjectGUID(objectGUID);
		final String thumbnailPhoto = lineSplit[0];
		adUserThumbnail.setThumbnailPhoto(thumbnailPhoto);

		adUserThumbnails.add(adUserThumbnail);

		if(adUserThumbnails.size() >= adUserThumbnailBufferSize){
			flushAdUserThumbnailBuffer();
		}
	}

	private void flushAdUserThumbnailBuffer(){
		if(!adUserThumbnails.isEmpty()) {
			adUserThumbnailservice.upsertBulk(adUserThumbnails);
			monitor.addDataReceived(getMonitorId(), new JobDataReceived("User Thumbnails", adUserThumbnails.size(), "Users"));
			adUserThumbnails.clear();
		}
	}
	
	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}


	private class AdUserThumbnailProcessJobHandler implements ActiveDirectoryResultHandler {

		private AdUserThumbnailProcessJobHandler() {
		}

		@Override
		public void handleAttributes(Attributes attributes) throws NamingException, IOException {
			if (attributes != null) {
				StringBuilder line = new StringBuilder();

				for (NamingEnumeration<? extends Attribute> index = attributes.getAll(); index.hasMoreElements(); ) {
					Attribute atr = index.next();

					String value = DatatypeConverter.printBase64Binary((byte[]) atr.get(0));

					line.append(value);
					line.append("|");
				}
				line.deleteCharAt(line.length() - 1);

				try {
					processLine(line.toString());
				} catch (Exception e) {
					logger.warn("Process line Fail - {}", e.getMessage());


				}
			}
		}

		@Override
		public void finishHandling() {
			// do nothing
		}
	}
}




