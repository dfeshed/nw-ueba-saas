package fortscale.collection.jobs.ad;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.ad.AdUserThumbnail;
import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;
import fortscale.domain.ad.dao.AdUserThumbnailRepository;
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@DisallowConcurrentExecution
public class AdUserThumbnailProcessJob extends FortscaleJob implements ActiveDirectoryResultHandler {

	private static Logger logger = Logger.getLogger(AdUserThumbnailProcessJob.class);
		
	@Autowired
	private AdUserThumbnailRepository adUserThumbnailRepository;

	@Autowired
	private ActiveDirectoryService activeDirectoryService;

	@Value("${users.ou.filter:}")
    private String ouUsersFilter;

	private String ldapFieldSeperator;
	
	private int adUserThumbnailBufferSize;

	private String filter;

	private String adFields;
	
	private List<AdUserThumbnail> adUserThumbnails = new ArrayList<>();
	
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		
		ldapFieldSeperator = jobDataMapExtension.getJobDataMapStringValue(map, "ldapFieldSeperator");
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

		getFromActiveDirectory(null, filter,adFields,-1);
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
	private void getFromActiveDirectory(BufferedWriter fileWriter, String filter, String adFields, int resultLimit) throws Exception {
		try {
			activeDirectoryService.getFromActiveDirectory(filter, adFields, resultLimit, this);
		} catch (Exception e) {
			final String errorMessage = this.getClass().getSimpleName() + " failed. Failed to fetch from Active Directory";
			logger.error(errorMessage);
			throw new JobExecutionException(errorMessage, e);
		}
	}

	private void processLine(String line){
		String lineSplit[] = StringUtils.split(line, ldapFieldSeperator);
		if(lineSplit.length != 2){
			return;
		}
		AdUserThumbnail adUserThumbnail = new AdUserThumbnail();
		adUserThumbnail.setThumbnailPhoto(lineSplit[0]);
		adUserThumbnail.setObjectGUID(lineSplit[1]);

		adUserThumbnails.add(adUserThumbnail);

		if(adUserThumbnails.size() >= adUserThumbnailBufferSize){
			flushAdUserThumbnailBuffer();
		}
	}

	private void flushAdUserThumbnailBuffer(){
		if(!adUserThumbnails.isEmpty()){
			adUserThumbnailRepository.save(adUserThumbnails);
			monitor.addDataReceived(getMonitorId(), new JobDataReceived("User Thumbnails", new Integer(adUserThumbnails.size()), "Users"));
			adUserThumbnails.clear();
		}
	}
	
	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}

	@Override
	public void handleAttributes(Attributes attributes) throws NamingException, IOException {
		if (attributes != null) {
			StringBuilder line = new StringBuilder();

			for (NamingEnumeration<? extends Attribute> index = attributes.getAll(); index.hasMoreElements(); ) {
				Attribute atr = index.next();

				String value = DatatypeConverter.printBase64Binary((byte[]) atr.get(0));;

				line.append(value);
				line.append("|");
			}
			line.deleteCharAt(line.length()-1);

			try {
				//process line
				processLine(line.toString());
			}
			catch(Exception e)
			{
				logger.warn("Process line Fail - {}", e.getMessage());


			}
		}
	}

	@Override
	public void finishHandling() {
		// do nothing
	}
}




