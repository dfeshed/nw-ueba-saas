package fortscale.collection.jobs;

import fortscale.domain.fetch.FetchConfiguration;
import fortscale.domain.fetch.FetchConfigurationRepository;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.time.DateUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Amir Keren on 4/4/16.
 */
@DisallowConcurrentExecution
public abstract class FetchJob extends FortscaleJob {

	protected static Logger logger = LoggerFactory.getLogger(FetchJob.class);

	@Autowired
	protected FetchConfigurationRepository fetchConfigurationRepository;

	@Autowired
	protected ApplicationConfigurationService applicationConfigurationService;

	@Value("${collection.fetch.data.path}")
	protected String outputPath;

	// time limits sends to siem (can be epoch/dates/spalnk constant as -1h@h) - in the case of manual run, this parameters will be used
	protected String earliest;
	protected String latest;
	protected String savedQuery;
	protected String returnKeys;
	protected String sortShellScript;
	protected String filenameFormat;
	protected String delimiter;
	//the type (data source) to bring saved configuration for.
	protected String type;
	// time limits as dates to allow easy paging - will be used in continues run
	protected Date earliestDate;
	protected Date latestDate;
	//time interval to bring in one fetch (uses for both regular single fetch, and paging in the case of miss fetch).
	//for manual fetch with time frame given as a run parameter will keep the -1 default and the time frame won't be paged.
	protected int fetchIntervalInSeconds = -1;
	protected boolean encloseQuotes = true;
	//indicate if still have more pages to go over and fetch
	protected boolean keepFetching = false;

	protected abstract void startFetch() throws Exception;

	@Override
	protected void runSteps() throws Exception {
		startFetch();
	}

	protected void getRunTimeFrameFromMongo(JobDataMap map) throws JobExecutionException {
		type = jobDataMapExtension.getJobDataMapStringValue(map, "type");
		//time back (default 1 hour)
		fetchIntervalInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchIntervalInSeconds", 3600);
		int ceilingTimePartInt = jobDataMapExtension.getJobDataMapIntValue(map, "ceilingTimePartInt", Calendar.HOUR);
		int fetchDiffInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchDiffInSeconds", 0);
		//set fetch until the ceiling of now (according to the given interval
		latestDate = DateUtils.ceiling(new Date(), ceilingTimePartInt);
		//shift the date by the configured diff
		latestDate = DateUtils.addSeconds(latestDate,-1*fetchDiffInSeconds);
		keepFetching = true;
		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(type);
		if (fetchConfiguration != null) {
			earliest = fetchConfiguration.getLastFetchTime();
			earliestDate = new Date(TimestampUtils.convertToMilliSeconds(Long.parseLong(earliest)));
		}
		else {
			earliestDate = DateUtils.addSeconds(latestDate, -1 * fetchIntervalInSeconds);
		}
	}

	protected void sortOutput() throws InterruptedException {
		if (outputTempFile.length()==0) {
			logger.info("deleting empty output file {}", outputTempFile.getName());
			if (!outputTempFile.delete())
				logger.warn("cannot delete empty file {}", outputTempFile.getName());
		} else {
			Process pr =  runCmd(null, sortShellScript, outputTempFile.getAbsolutePath(), outputFile.getAbsolutePath());
			if(pr == null){
				logger.error("Failed to sort output of file {} using {}", outputTempFile.getAbsolutePath(), sortShellScript);
				addError(String.format("got the following error while running the shell command %s.",sortShellScript));
			} else if(pr.waitFor() != 0){ // wait for process to finish
				// error (return code is different than 0)
				handleCmdFailure(pr, sortShellScript);
			}
			outputTempFile.delete();

		}
	}

	protected void renameOutput() {
		if (outputTempFile.length()==0) {
			logger.info("deleting empty output file {}", outputTempFile.getName());
			if (!outputTempFile.delete())
				logger.warn("cannot delete empty file {}", outputTempFile.getName());
		} else {
			outputTempFile.renameTo(outputFile);
		}
	}

	protected void updateMongoWithCurrentFetchProgress() {
		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(type);
		latest = TimestampUtils.convertSplunkTimeToUnix(latest);
		if (fetchConfiguration == null) {
			fetchConfiguration = new FetchConfiguration(type, latest);
		} else {
			fetchConfiguration.setLastFetchTime(latest);
		}
		fetchConfigurationRepository.save(fetchConfiguration);
		if (earliestDate != null && latestDate != null) {
			if (earliestDate.after(latestDate) || earliestDate.equals(latestDate)) {
				keepFetching = false;
			}
		}
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 3;
	}

	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}

}