package fortscale.collection.jobs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.utils.logging.Logger;

public abstract class FortscaleJob implements Job {
	private static Logger logger = Logger.getLogger(FortscaleJob.class);
	
	@Autowired 
	protected JobProgressReporter monitor;
	
	private String monitorId;
	
	private String stepName;
	
	private int stepIndex = 1;
		
	
	protected abstract void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException;
	
	protected abstract int getTotalNumOfSteps();
	
	protected abstract void runSteps() throws Exception;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getJobParameters(jobExecutionContext);
		
		String monitorId = startMonitoring(jobExecutionContext, getTotalNumOfSteps());
		
		setMonitorId(monitorId);
		try{
			runSteps();
		} catch (Exception e) {
			logger.error(String.format("while running the step %s, got the following exception", stepName), e);
			monitor.error(monitorId, stepName, String.format("while running the step %s, got the following exception %s", stepName, e.getMessage()));
		}
		
		
		monitor.finishJob(monitorId);
	}
	
		
	protected Process runCmd(File workingDir, String... commands){
		ProcessBuilder processBuilder = null;
		Process pr = null;	
		try {
			processBuilder = new ProcessBuilder(commands);
			if(workingDir != null){
				processBuilder.directory(workingDir);
			}
			pr = processBuilder.start();
			pr.waitFor();

		} catch (Exception e) {
			String cmd = StringUtils.join(commands, " ");
			logger.error(String.format("while running the command \"%s\", got the following exception", cmd), e);
			monitor.error(monitorId, stepName, String.format("while running the command %s, got the following exception %s", cmd, e.getMessage()));
			return null;
		}		
		
		return pr;
	}
	
	protected File ensureOutputDirectoryExists(String outputPath) throws JobExecutionException {
		File outputDir = new File(outputPath);
		try {
			if (!outputDir.exists()) {
				// try to create output directory
				outputDir.mkdirs();
			}
			
			return outputDir;
		} catch (SecurityException e) {
			logger.error("cannot create output path - " + outputPath, e);
			// stop execution, notify scheduler not to re-fire immediately
			throw new JobExecutionException(e,  false); 
		}
	}
	
	protected File createOutputFile(File outputDir, String filename) throws JobExecutionException {	
//		outputTempFile = new File(outputDir, filename + ".part");
		File outputFile = new File(outputDir, filename);
		
		try {
			if (!outputFile.createNewFile()) {
				logger.error("cannot create output file {}", outputFile);
				throw new JobExecutionException("cannot create output file " + outputFile.getAbsolutePath());
			}
					
		} catch (IOException e) {
			logger.error("error creating file " + outputFile.getPath(), e);
			throw new JobExecutionException("cannot create output file " + outputFile.getAbsolutePath());
		}
		
		return outputFile;
	}
	
	protected void renameOutput(File oldOutputFile, File newOutputFile) {
		if (oldOutputFile.length()==0) {
			logger.info("deleting empty output file {}", oldOutputFile.getName());
			if (!oldOutputFile.delete())
				logger.warn("cannot delete empty file {}", oldOutputFile.getName());
		} else {
			oldOutputFile.renameTo(newOutputFile);
		}
	}
	
	/**
	 * Gets list of files in the input folder sorted according to the time stamp
	 */
	protected File[] listFiles(String inputPath, final String filesFilter) throws JobExecutionException {
		File inputDir = new File(inputPath);
		if (!inputDir.exists() || !inputDir.isDirectory()) {
			logger.error("input path {} does not exists", inputPath);
			throw new JobExecutionException(String.format("input path %s does not exists", inputPath));
		}

		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().matches(filesFilter);
			}
		};

		File[] files = inputDir.listFiles(filter);
		Arrays.sort(files);
		return files;
	}
	
	protected void monitorDataReceived(File output, String dataType){
		monitor.addDataReceived(monitorId, getJobDataReceived(output, dataType));
	}
	
	private JobDataReceived getJobDataReceived(File output, String dataType) {
		if (output.length() < 1024) {
			return new JobDataReceived(dataType, (int)output.length(), "Bytes");
		} else {
			int sizeInKB = (int) (output.length() / 1024);
			return new JobDataReceived(dataType, sizeInKB, "KB");
		}
	}
	

	private String startMonitoring(JobExecutionContext jobExecutionContext, int numOfSteps){
		// get the job group name to be used using monitoring 
		String sourceName = jobExecutionContext.getJobDetail().getKey().getGroup();
		String jobName = jobExecutionContext.getJobDetail().getKey().getName();
		String monitorId = monitor.startJob(sourceName, jobName, numOfSteps);
		
		return monitorId;
	}
	
	protected String getMonitorId() {
		return monitorId;
	}

	private void setMonitorId(String monitorId) {
		this.monitorId = monitorId;
	}

	public String getStepName() {
		return stepName;
	}

	public void startNewStep(String stepName) {
		logger.info("Running {} ", stepName);		
		this.stepName = stepName;
		monitor.startStep(monitorId, stepName, stepIndex);
		stepIndex++;
	}
	
	public void finishStep(){
		monitor.finishStep(monitorId, stepName);
	}

	
	
}
