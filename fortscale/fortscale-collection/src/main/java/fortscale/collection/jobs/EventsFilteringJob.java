package fortscale.collection.jobs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.kitesdk.morphline.api.Record;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class EventsFilteringJob extends GenericSecurityEventsJob{
	private static Logger logger = LoggerFactory.getLogger(EventsFilteringJob.class);
	private static final String FILTERED_PREFIX_FILE = "filtered_";
	private static final String TMP_FILE_SUFFIX = ".part";
	private BufferedWriter writer = null;
	
	
	@Override
	protected boolean processFile(File file) throws JobExecutionException, IOException{
		boolean ret = false;
		File outputFile = createTempOutputFile(FILTERED_PREFIX_FILE, file);
		writer = new BufferedWriter(new FileWriter(outputFile));
		try {
			ret = super.processFile(file);
		}
		finally {
			writer.close();
		}
		renameTmpFile(outputFile);
		return ret;
	}
	@Override
	protected Record processLine(String line) throws IOException {
		// process each line
		Record record = super.processLine(line);
		if(record == null){
			return null;
		}
		
		writer.write(line);
		writer.newLine();
		
		return record;
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
	
	private void renameTmpFile(File outputTempFile){
		String filename = outputTempFile.getName();
		filename = filename.substring(0, filename.length() - TMP_FILE_SUFFIX.length());
		File outputFile = new File(new File(inputPath), filename);
		renameOutput(outputTempFile, outputFile);
		
	}
}
