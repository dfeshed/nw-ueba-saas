package fortscale.collection.jobs.ad;

import org.quartz.DisallowConcurrentExecution;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.logging.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.*;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by Amir Keren on 17/05/2015.
 */
@DisallowConcurrentExecution
public class AdFetchJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(AdFetchJob.class);
	private static final String OUTPUT_TEMP_FILE_SUFFIX = ".part";
	private File outputTempFile;
	private File outputFile;
	//Job parameters:
	private String filenameFormat;
	private String outputPath;
	private String filter;
	private String adFields;

	@Autowired
	private AdConnections adConnections;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		// get parameters values from the job data map
		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		outputPath = jobDataMapExtension.getJobDataMapStringValue(map, "outputPath");
		filter = jobDataMapExtension.getJobDataMapStringValue(map, "filter");
		adFields = jobDataMapExtension.getJobDataMapStringValue(map, "adFields");
	}

	@Override
	protected int getTotalNumOfSteps(){
		return 2;
	}

	@Override
	protected void runSteps() throws Exception{
		boolean isSucceeded = prepareSinkFileStep();
		if (!isSucceeded){
			return;
		}
		isSucceeded = fetchAndWriteToFileStep();
		if (!isSucceeded){
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
		FileWriter fileWriter = new FileWriter(outputTempFile);
		for (AdConnection adConnection: adConnections.getAdConnections()) {
			String dcAddress = adConnection.getIp_address();
			dcAddress = "ldap://" + dcAddress + ":389";
			String baseSearch = adConnection.getDomain_base_search();
			String username = adConnection.getDomain_user();
			String password = adConnection.getDomain_password();
			password = fortscale.utils.EncryptionUtils.decrypt(password);
			Hashtable environment = new Hashtable();
			environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			environment.put(Context.PROVIDER_URL, dcAddress);
			environment.put(Context.SECURITY_PRINCIPAL, username);
			environment.put(Context.SECURITY_CREDENTIALS, password);
			LdapContext context = new InitialLdapContext(environment, null);
			SearchControls searchControls = new SearchControls();
			String[] adFieldsArray = adFields.split(",");
			searchControls.setReturningAttributes(adFieldsArray);
			searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration answer = context.search(baseSearch, filter, searchControls);
			while (answer != null && answer.hasMore()) {
				SearchResult result = (SearchResult)answer.next();
				Attributes attributes = result.getAttributes();
				for (int i = 0; i < adFieldsArray.length; i++) {
					fileWriter.append(String.valueOf(attributes.get(adFieldsArray[i])));
					if (i < adFieldsArray.length - 1) {
						fileWriter.append(",");
					}
				}
				fileWriter.append("\n");
			}
			context.close();
		}
		fileWriter.flush();
		fileWriter.close();
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