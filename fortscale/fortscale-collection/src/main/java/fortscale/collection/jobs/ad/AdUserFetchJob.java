package fortscale.collection.jobs.ad;

import java.io.File;
import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import fortscale.collection.JobDataMapExtension;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.logging.Logger;




@DisallowConcurrentExecution
public class AdUserFetchJob extends FortscaleJob {
	private static Logger logger = Logger.getLogger(AdUserFetchJob.class);
	
	private static final String OUTPUT_TEMP_FILE_SUFFIX = ".part";
	
	@Autowired
	private JobDataMapExtension jobDataMapExtension;
	
	private String filenameFormat;
	private String outputPath;
	
	private File outputTempFile;
	private File outputFile;
	
	@Value("${collection.shell.scripts.dir.path}/ldapUserSearch.sh")
	private String ldapUserSearchShellScript;
	
	
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		
		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		outputPath = jobDataMapExtension.getJobDataMapStringValue(map, "outputPath");	
	}

	@Override
	protected int getTotalNumOfSteps(){
		return 2;
	}
		
	@Override
	protected void runSteps() throws Exception{
		
		boolean isSucceeded = prepareSinkFileStep(); 
		if(!isSucceeded){
			return;
		}
		
		isSucceeded = fetchAndWriteToFileStep();
		if(!isSucceeded){
			return;
		}
		
	}
	
	private boolean prepareSinkFileStep() throws JobExecutionException{
		startNewStep("Prepare sink file");
		
		logger.debug("creating output file at {}", outputPath);
		// ensure output path exists
		File outputDir = ensureOutputDirectoryExists(outputPath);
		
		// generate filename according to the job name and time
		String filename = String.format(filenameFormat, (new Date()).getTime());
		
		// try to create temp output file
		outputTempFile = createOutputFile(outputDir, String.format("%s%s",filename,OUTPUT_TEMP_FILE_SUFFIX));
		logger.debug("created output temp file at {}", outputTempFile.getAbsolutePath());
		
		outputFile = new File(outputDir, filename);
		
		finishStep();
		
		return true;
	}
	
	private boolean fetchAndWriteToFileStep(){
		startNewStep("Fetch and Write to file");
		
		Process pr =  runCmd(null, ldapUserSearchShellScript, outputTempFile.getAbsolutePath());
		if(pr == null){
			return false;
		}
		renameOutput(outputTempFile, outputFile);
		
		monitorDataReceived(outputFile, "AdUser");
		
		finishStep();
		
		return true;
	}
	
}
