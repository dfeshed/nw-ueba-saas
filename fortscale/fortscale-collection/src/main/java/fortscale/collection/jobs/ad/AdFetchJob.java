package fortscale.collection.jobs.ad;

import java.io.File;
import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.logging.Logger;




@DisallowConcurrentExecution
public class AdFetchJob extends FortscaleJob {
	private static Logger logger = Logger.getLogger(AdFetchJob.class);
	
	private static final String OUTPUT_TEMP_FILE_SUFFIX = ".part";
	
	
	private File outputTempFile;
	private File outputFile;
	
	//Job parameters:
	private String filenameFormat;
	private String outputPath;
	private String ldapUserSearchShellScript;
	
	
	
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		
		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		outputPath = jobDataMapExtension.getJobDataMapStringValue(map, "outputPath");	
		ldapUserSearchShellScript = jobDataMapExtension.getJobDataMapStringValue(map, "ldapUserSearchShellScript");
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
	
	private boolean fetchAndWriteToFileStep() throws InterruptedException{
		startNewStep("Fetch and Write to file");
		
		//TODO: Handle errors
		Process pr =  runCmd(null, ldapUserSearchShellScript, outputTempFile.getAbsolutePath());
		if(pr == null){
			return false;
		}
		pr.waitFor();
		
		renameOutput(outputTempFile, outputFile);
		
		monitorDataReceived(outputFile, "Ad");
		
		finishStep();
		
		return true;
	}
	
	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}
}
