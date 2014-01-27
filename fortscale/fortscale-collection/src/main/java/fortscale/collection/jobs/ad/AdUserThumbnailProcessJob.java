package fortscale.collection.jobs.ad;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.ad.AdUserThumbnail;
import fortscale.domain.ad.dao.AdUserThumbnailRepository;

@DisallowConcurrentExecution
public class AdUserThumbnailProcessJob extends FortscaleJob {
		
	@Autowired
	private AdUserThumbnailRepository adUserThumbnailRepository;

	
	
	@Value("${collection.shell.scripts.dir.path}/ldapUserThumbnail.sh")
	private String ldapUserThumbnail;
	
	private String ldapFieldSeperator;
	
	private int adUserThumbnailBufferSize;
	
	private List<AdUserThumbnail> adUserThumbnails = new ArrayList<>();
	
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		
		ldapFieldSeperator = jobDataMapExtension.getJobDataMapStringValue(map, "ldapFieldSeperator");
		adUserThumbnailBufferSize = jobDataMapExtension.getJobDataMapIntValue(map, "adUserThumbnailBufferSize");
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 1;
	}

	@Override
	protected void runSteps() throws Exception {
		startNewStep("etl");
		
		Process pr =  runCmd(null, ldapUserThumbnail);
		BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = "";			
		while ((line = reader.readLine())!= null) {
			processLine(line);
		}
		flushAdUserThumbnailBuffer();
		finishStep();
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
			adUserThumbnails.clear();
		}
	}

}
