package fortscale.collection.jobs.ad;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.ad.AdUserThumbnail;
import fortscale.domain.ad.dao.AdUserThumbnailRepository;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.*;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@DisallowConcurrentExecution
public class AdUserThumbnailProcessJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(AdUserThumbnailProcessJob.class);
		
	@Autowired
	private AdUserThumbnailRepository adUserThumbnailRepository;

	@Autowired
	private AdConnections adConnections;

    
	@Value("${users.ou.filter:}")
    private String ouUsersFilter;

	private String ldapFieldSeperator;
	
	private int adUserThumbnailBufferSize;

	private String filter;

	private String adFields;
	
	private List<AdUserThumbnail> adUserThumbnails = new ArrayList<>();
	
	
	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		// get parameters values from the job data map
		
		ldapFieldSeperator = jobDataMapExtension.getJobDataMapStringValue(map, "ldapFieldSeperator");
		adUserThumbnailBufferSize = jobDataMapExtension.getJobDataMapIntValue(map, "adUserThumbnailBufferSize");

		//AD search filter
		filter = jobDataMapExtension.getJobDataMapStringValue(map, "filter");
		//AD selected fields
		adFields = jobDataMapExtension.getJobDataMapStringValue(map, "adFields");
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 1;
	}

	@Override
	protected void runSteps() throws Exception {
		startNewStep("Fetch Thumbnail from AD");

		fetchFromAD(filter,adFields,-1);
        flushAdUserThumbnailBuffer();

		finishStep();


	}



	/**
	 * This is the main method for the job. It connects to all of the domains by iterating
	 * over each one of them and attempting to connect to their DCs until one such connection is successful.
	 * It then performs the requested search according to the filter and saves the results in fileWriter object.
	 *

	 *
	 * @param  _filter		   The Active Directory search filter (which object class is required)
	 * @param  _adFields	   The Active Directory attributes to return in the search
	 * @param  resultLimit	   A limit on the search results (mostly for testing purposes) should be <= 0 for no limit
	 */
	private void fetchFromAD( String _filter, String _adFields, int resultLimit) throws Exception {
		byte[] cookie;
		int pageSize = 1000;
		int totalRecords = 0;



		logger.info("Connecting to domain controllers");

		for (AdConnection adConnection: this.adConnections.getAdConnections()) {
			logger.info("Fetching from {}", adConnection.getDomainName());

			LdapContext context = null;

			boolean connected = false;

			int records = 0;

			//Loop over the DCs address
			for (String dcAddress: adConnection.getIpAddresses()) {
				logger.info("Trying to connect to domain controller at {}", dcAddress);

				connected = true;

				//Todo - Need to export port into configuration under adConnections.json
				dcAddress = "ldap://" + dcAddress + ":389";

				String username = adConnection.getDomainUser() + "@" + adConnection.getDomainName();
				String password = adConnection.getDomainPassword();
				password = fortscale.utils.EncryptionUtils.decrypt(password);
				Hashtable<String, String> environment = new Hashtable<String, String>();

				environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
				environment.put(Context.PROVIDER_URL, dcAddress);
				environment.put(Context.SECURITY_PRINCIPAL, username);
				environment.put(Context.SECURITY_CREDENTIALS, password);
				environment.put("java.naming.ldap.attributes.binary", "objectSid objectGUID");
				try {
					context = new InitialLdapContext(environment, null);
				} catch (javax.naming.CommunicationException ex) {
					logger.warn("Connection failed - {}", ex.getMessage());
					connected = false;
				}
				if (connected) {
					break;
				}
			}

			if (connected) {
				logger.info("Connection established");
			} else {
				logger.error("Failed to connect to any domain controller for {}", adConnection.getDomainName());
				throw new Exception ("Failed to connect to any domain controller for " + adConnection.getDomainName());
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
				NamingEnumeration<SearchResult> answer = context.search(baseSearch, _filter, searchControls);
				while (answer != null && answer.hasMoreElements() && answer.hasMore()) {
					SearchResult result = (SearchResult)answer.next();
					Attributes attributes = result.getAttributes();
					handleAttributes(attributes);

					records++;
				}
				cookie = parseControls(context.getResponseControls());
				context.setRequestControls(new Control[]{new PagedResultsControl(pageSize, cookie, Control.CRITICAL)});
			} while ((cookie != null) && (cookie.length != 0));
			context.close();
			totalRecords += records;
			logger.info("Fetched {} records for domain {}", records, adConnection.getDomainName());
		}

		logger.info("Fetched a total of {} records", totalRecords);
	}

	private void handleAttributes(Attributes attributes) throws NamingException,IOException {

		if (attributes != null) {
			StringBuilder line = new StringBuilder();

			for (NamingEnumeration<? extends Attribute> index = attributes.getAll(); index.hasMoreElements(); ) {
				Attribute atr = (Attribute) index.next();
				String key = atr.getID();
				String value = DatatypeConverter.printBase64Binary((byte[]) atr.get(0));;


				line.append(key + ": " + value);
				line.append("|");


			}
			line.deleteCharAt(line.length()-1);

			try {
				//process line
				processLine(line.toString());
			}
			catch(Exception e)
			{
				logger.warn("Process line Fail - {}",e.getMessage());


			}
		}


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
			monitor.addDataReceived(getMonitorId(), new JobDataReceived("User Thumbnails", adUserThumbnails.size(), "Users"));
			adUserThumbnails.clear();
		}
	}
	
	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}

}
