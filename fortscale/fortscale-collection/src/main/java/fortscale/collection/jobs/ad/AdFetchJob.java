
package fortscale.collection.jobs.ad;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.ad.dao.ActiveDirectoryResultHandler;
import fortscale.services.ActiveDirectoryService;
import fortscale.utils.logging.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Amir Keren on 17/05/2015.
 */
@DisallowConcurrentExecution
public class AdFetchJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(AdFetchJob.class);

	private static final String OUTPUT_TEMP_FILE_SUFFIX = ".part";

	@Autowired
	private ActiveDirectoryService activeDirectoryService;

	private File outputTempFile;
	private File outputFile;
	//Job parameters:
	private String filenameFormat;
	private String outputPath;
	private String filter;
	private String adFields;
	private BufferedWriter fileWriter;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		// get parameters values from the job data map
		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		outputPath = jobDataMapExtension.getJobDataMapStringValue(map, "outputPath");
		//AD search filter
		filter = jobDataMapExtension.getJobDataMapStringValue(map, "filter");
		//AD selected fields
		adFields = jobDataMapExtension.getJobDataMapStringValue(map, "adFields");
	}

	@Override
	protected int getTotalNumOfSteps(){
		return 2;
	}

	@Override
	protected void runSteps() throws Exception{
		boolean isSucceeded = prepareSinkFileStep();
		if (!isSucceeded) {
			return;
		}
		isSucceeded = fetchAndWriteToFileStep();
		if (!isSucceeded) {
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
		outputTempFile = createOutputFile(outputDir, String.format("%s%s", filename, OUTPUT_TEMP_FILE_SUFFIX));
		logger.debug("created output temp file at {}", outputTempFile.getAbsolutePath());
		outputFile = new File(outputDir, filename);
		finishStep();
		return true;
	}

	private boolean fetchAndWriteToFileStep() throws Exception {
		startNewStep("Fetch and Write to file");
		FileWriter file = new FileWriter(outputTempFile);
		fileWriter = new BufferedWriter(file);
		getFromActiveDirectory(filter, adFields, -1);
		renameOutput(outputTempFile, outputFile);
		monitorDataReceived(outputFile, "Ad");
		finishStep();
		return true;
	}

	/**
	 * This is the main method for the job. It connects to all of the domains by iterating
	 * over each one of them and attempting to connect to their DCs until one such connection is successful.
	 * It then performs the requested search according to the filter and saves the results using the {@code fileWriter}.
	 *
	 * @param  filter		   The Active Directory search filter (which object class is required)
	 * @param  adFields	   The Active Directory attributes to return in the search
	 * @param  resultLimit	   A limit on the search results (mostly for testing purposes) should be <= 0 for no limit
	 */
	private void getFromActiveDirectory(String filter, String
			adFields, int resultLimit) throws Exception {
		try {
			activeDirectoryService.getFromActiveDirectory(filter, adFields, resultLimit, new AdFetchJobHandler());
		} catch (Exception e) {
			final String errorMessage = this.getClass().getSimpleName() +
					" failed. Failed to fetch from Active Directory";
			logger.error(errorMessage);
			throw new JobExecutionException(errorMessage, e);
		}
	}



	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}

	private void appendAllAttributeElements(BufferedWriter fileWriter, String key, NamingEnumeration<?> values)
			throws IOException {
		boolean first = true;
		while (values.hasMoreElements()) {
			String value = (String)values.nextElement();
			if (value.contains("\n") || value.contains("\r")) {
				value = DatatypeConverter.printBase64Binary(value.getBytes());
			}
			if (first) {
				first = false;
			} else {
				fileWriter.append("\n");
			}
			fileWriter.append(key).append(": ").append(value);
		}
	}

	private class AdFetchJobHandler implements ActiveDirectoryResultHandler {

		private AdFetchJobHandler() {}

		@Override
		//auxiliary method that handles the current response from the server
		public void handleAttributes(Attributes attributes) throws NamingException, IOException {
			if (attributes != null) {
				for (NamingEnumeration<? extends Attribute> index = attributes.getAll(); index.hasMoreElements(); ) {
					Attribute atr = index.next();
					String key = atr.getID();
					NamingEnumeration<?> values = atr.getAll();
					if (key.equals("member")) {
						appendAllAttributeElements(fileWriter, key, values);
					} else if (values.hasMoreElements()) {
						String value;
						if (key.equals("distinguishedName")) {
							value = (String) values.nextElement();
							if (value.contains("\n") || value.contains("\r")) {
								value = DatatypeConverter.printBase64Binary(value.getBytes());
							}
							fileWriter.append("dn: ").append(value);
							fileWriter.append("\n");
							fileWriter.append(key).append(": ").append(value);
						} else if (key.equals("objectGUID") || key.equals("objectSid")) {
							value = DatatypeConverter.printBase64Binary((byte[]) values.nextElement());
							fileWriter.append(key).append(": ").append(value);
						} else {
							appendAllAttributeElements(fileWriter, key, values);
						}

					}
					fileWriter.append("\n");
				}
			}
			fileWriter.append("\n");
		}

		@Override
		public void finishHandling() {
			try {
				if (fileWriter != null) {
					fileWriter.flush();
					fileWriter.close();
				}
			} catch (IOException e) {
				logger.warn("Failed to finish Handling.", e);
			}
		}

	}

}