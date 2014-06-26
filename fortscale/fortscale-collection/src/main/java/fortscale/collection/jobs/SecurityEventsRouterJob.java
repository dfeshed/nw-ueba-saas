package fortscale.collection.jobs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fortscale.collection.morphlines.RecordExtensions;

@DisallowConcurrentExecution
public class SecurityEventsRouterJob extends GenericSecurityEventsJob{
	private static Logger logger = LoggerFactory.getLogger(SecurityEventsRouterJob.class);
	
	private static final String COMPUTER_PREFIX_FILE = "computer_";
	private static final String USER_PREFIX_FILE = "user_";
	private static final String TMP_FILE_SUFFIX = ".part";
	
	private BufferedWriter currentCompWriter = null;
	private BufferedWriter currentUserWriter = null;
	
	
	@Override
	protected boolean processFile(File file) throws IOException, JobExecutionException {
		boolean ret = false;
		
		File compTmpFile = createTempOutputFile(COMPUTER_PREFIX_FILE,file);
		currentCompWriter = new BufferedWriter(new FileWriter(compTmpFile));
		File userTmpFile = createTempOutputFile(USER_PREFIX_FILE,file);
		currentUserWriter = new BufferedWriter(new FileWriter(userTmpFile));
		
		try {
			ret = super.processFile(file);
		} finally {
			currentCompWriter.close();
			currentUserWriter.close();
		}
		
		renameTmpFile(compTmpFile);
		renameTmpFile(userTmpFile);
		
		return ret;
	}
	
	private void renameTmpFile(File outputTempFile){
		String filename = outputTempFile.getName();
		filename = filename.substring(0, filename.length() - TMP_FILE_SUFFIX.length());
		File outputFile = new File(new File(inputPath), filename);
		renameOutput(outputTempFile, outputFile);
		
	}
	
	private File createTempOutputFile(String prefix, File inFile) throws JobExecutionException {	
		// generate filename according to the job name and time
		String filename = String.format("%s%s%s", prefix, inFile.getName(),TMP_FILE_SUFFIX);
		
		File outputFile = new File(new File(inputPath), filename);
		
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
	
	@Override
	protected Record processLine(String line) throws IOException {
		// process each line
		Record record = super.processLine(line);
		if(record == null){
			return null;
		}
		
		Boolean isComputer = RecordExtensions.getBooleanValue(record, "isComputer");
		BufferedWriter writer = null;
		if(isComputer){
			Object eventCodeObj = record.getFirstValue("eventCode");
			if (!"4768".equals(eventCodeObj)) {
				return null;
			}
			writer = currentCompWriter;
		} else{
			writer = currentUserWriter;
		}
		writer.write(line);
		writer.newLine();
		
		return record;
	}
}
