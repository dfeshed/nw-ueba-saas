package fortscale.collection.jobs.ad;

import org.springframework.beans.factory.annotation.Autowired;
import org.quartz.DisallowConcurrentExecution;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.utils.logging.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.*;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
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
		FileWriter file = new FileWriter(outputTempFile);
		BufferedWriter fileWriter = new BufferedWriter(file);
		fetchFromAD(adConnections, fileWriter, filter, adFields, -1);
		renameOutput(outputTempFile, outputFile);
		monitorDataReceived(outputFile, "Ad");
		finishStep();
		return true;
	}

	/**
	 * This is the main method for the job. It connects to all of the domains by iterating
	 * over each one of them and attempting to connect to their DCs until one such connection is successful.
	 * It then performs the requested search according to the filter and saves the results in fileWriter object.
	 *
	 * @param  _adConnections  A collection of all of the domain connection strings
	 * @param  fileWriter      An object to save the results to (could be a file, STDOUT, String etc.)
	 * @param  _filter		   The Active Directory search filter (which object class is required)
	 * @param  _adFields	   The Active Directory attributes to return in the search
	 * @param  resultLimit	   A limit on the search results (mostly for testing purposes) should be <= 0 for no limit
	 */
	public void fetchFromAD(AdConnections _adConnections, BufferedWriter fileWriter, String _filter, String
			_adFields, int resultLimit) throws Exception {
		byte[] cookie;
		int pageSize = 1000;
		int totalRecords = 0;
		logger.debug("Connecting to domain controllers");
		for (AdConnection adConnection: _adConnections.getAdConnections()) {
			logger.debug("Fetching from {}", adConnection.getDomainName());
			LdapContext context = null;
			boolean connected = false;
			int records = 0;
			for (String dcAddress: adConnection.getIpAddresses()) {
				logger.debug("Trying to connect to domain controller at {}", dcAddress);
				connected = true;
				dcAddress = "ldap://" + dcAddress + ":389";
				String username = adConnection.getDomainUser() + "@" + adConnection.getDomainName();
				String password = adConnection.getDomainPassword();
				password = fortscale.utils.EncryptionUtils.decrypt(password);
				Hashtable environment = new Hashtable();
				environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
				environment.put(Context.PROVIDER_URL, dcAddress);
				environment.put(Context.SECURITY_PRINCIPAL, username);
				environment.put(Context.SECURITY_CREDENTIALS, password);
				environment.put("java.naming.ldap.attributes.binary", "objectSid objectGUID");
				try {
					context = new InitialLdapContext(environment, null);
				} catch (javax.naming.CommunicationException ex) {
					logger.debug("Connection failed");
					connected = false;
				}
				if (connected) {
					break;
				}
			}
			if (connected) {
				logger.debug("Connection established");
			} else {
				logger.debug("Failed to connect to any domain controller for {}", adConnection.getDomainName());
				continue;
			}
			String baseSearch = adConnection.getDomainBaseSearch();
			context.setRequestControls(new Control[]{new PagedResultsControl(pageSize, Control.CRITICAL)});
			SearchControls searchControls = new SearchControls();
			searchControls.setReturningAttributes(_adFields.split(","));
			searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			if (resultLimit > 0) {
				searchControls.setCountLimit(resultLimit);
			}
			do {
				NamingEnumeration answer = context.search(baseSearch, _filter, searchControls);
				while (answer != null && answer.hasMoreElements() && answer.hasMore()) {
					SearchResult result = (SearchResult)answer.next();
					Attributes attributes = result.getAttributes();
					handleAttributes(fileWriter, attributes);
					records++;
				}
				cookie = parseControls(context.getResponseControls());
				context.setRequestControls(new Control[]{new PagedResultsControl(pageSize, cookie, Control.CRITICAL)});
			} while ((cookie != null) && (cookie.length != 0));
			context.close();
			totalRecords += records;
			logger.debug("Fetched {} records for domain {}", records, adConnection.getDomainName());
		}
		fileWriter.flush();
		fileWriter.close();
		logger.debug("Fetched a total of {} records", totalRecords);
	}

	//auxiliary method that handles the current response from the server
	private void handleAttributes(BufferedWriter fileWriter, Attributes attributes) throws NamingException,IOException {
		if (attributes != null) {
            for (NamingEnumeration index = attributes.getAll(); index.hasMoreElements();) {
                Attribute atr = (Attribute)index.next();
                String key = atr.getID();
                Enumeration values = atr.getAll();
				if (key.equals("member")) {
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
                        fileWriter.append(key + ": " + value);
                    }
                } else if (values.hasMoreElements()) {
                    String value;
                    if (key.equals("distinguishedName")) {
                        value = (String)values.nextElement();
						if (value.contains("\n") || value.contains("\r")) {
							value = DatatypeConverter.printBase64Binary(value.getBytes());
						}
                        fileWriter.append("dn: " + value);
                        fileWriter.append("\n");
                    } else if (key.equals("objectGUID") || key.equals("objectSid")) {
                        value = DatatypeConverter.printBase64Binary((byte[]) values.nextElement());
                    } else {
                        value = (String)values.nextElement();
						if (value.contains("\n") || value.contains("\r")) {
							value = DatatypeConverter.printBase64Binary(value.getBytes());
						}
                    }
                    fileWriter.append(key + ": " + value);
                }
                fileWriter.append("\n");
            }
        }
		fileWriter.append("\n");
	}

	//used to determine if an additional page of results exists
	private byte[] parseControls(Control[] controls) throws NamingException {
		byte[] serverCookie = null;
		if (controls != null) {
			for (int i = 0; i < controls.length; i++) {
				if (controls[i] instanceof PagedResultsResponseControl) {
					PagedResultsResponseControl pagedResultsResponseControl = (PagedResultsResponseControl)controls[i];
					serverCookie = pagedResultsResponseControl.getCookie();
				}
			}
		}
		return (serverCookie == null) ? new byte[0] : serverCookie;
	}

	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}

}