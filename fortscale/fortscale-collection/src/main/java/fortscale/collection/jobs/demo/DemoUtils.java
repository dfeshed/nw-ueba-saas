package fortscale.collection.jobs.demo;

import fortscale.domain.core.*;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
import fortscale.services.UserTagEnum;
import fortscale.services.exceptions.HdfsException;
import fortscale.services.impl.HdfsService;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.impala.ImpalaQuery;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static fortscale.utils.impala.ImpalaCriteria.gte;
import static fortscale.utils.impala.ImpalaCriteria.lte;

/**
 * Created by Amir Keren on 14/02/16.
 */
public class DemoUtils {

	private static Logger logger = Logger.getLogger(ScenarioGeneratorJob.class);

	public enum EventFailReason { TIME, FAILURE, SOURCE, DEST, COUNTRY, NONE, FILE_SIZE, TOTAL_PAGES, STATUS,
		ACTION_TYPE, USERNAME, OBJECT }
	public enum DataSource { kerberos_logins, ssh, vpn, amt }

	public static final DateTimeFormatter HDFS_FOLDER_FORMAT = DateTimeFormat.forPattern("yyyyMMdd");
	public static final DateTimeFormatter HDFS_TIMESTAMP_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	public static final String CONTEXT = "classpath*:META-INF/spring/collection-context.xml";
	public static final String SPLIT_STRATEGY = "fortscale.utils.hdfs.split.DailyFileSplitStrategy";
	public static final String NORMALIZED_USERNAME = "normalized_username";
	public static final String EPOCH_TIME_FIELD = "date_time_unix";
	public static final String SEPARATOR = ",";
	public static final String DATA_SOURCE_FIELD = "data_source";
	public static final String LAST_STATE_FIELD = "last_state";
	public static final String AGGREGATION_TOPIC = "fortscale-vpn-event-score-from-hdfs";
	public static final String ALERT_GENERATOR_TASK = "ALERT_GENERATOR";
	public static final int SLEEP_TIME = 1000 * 60 * 15;

	/**
	 *
	 * This method creates the actual csv line to write in HDFS for kerberos
	 *
	 * @param dt
	 * @param user
	 * @param srcMachine
	 * @param dstMachine
	 * @param score
	 * @param reason
	 * @param domain
	 * @param dc
	 * @param clientAddress
	 * @param failureCode
	 * @return
	 */
	public String buildKerberosHDFSLine(DateTime dt, User user, Computer srcMachine, String dstMachine,
			int score, EventFailReason reason, String domain, String dc, String clientAddress, String failureCode) {
		String srcClass = "Desktop";
		String dstClass = "Server";
		int dateTimeScore = 0;
		int failureCodeScore = 0;
		int normalizedSrcMachineScore = 0;
		int normalizedDstMachineScore = 0;
		boolean isNat = false;
		switch (reason) {
			case TIME: dateTimeScore = score; break;
			case FAILURE: failureCodeScore = score; break;
			case SOURCE: normalizedSrcMachineScore = score; break;
			case DEST: normalizedDstMachineScore = score; break;
		}
		int eventScore = score;
		long timestamp = new Date().getTime();
		String serviceId = domain + "\\" + dc;
		StringBuilder sb = new StringBuilder()
				.append(HDFS_TIMESTAMP_FORMAT.print(dt)).append(SEPARATOR)
				.append(dt.getMillis() / 1000).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(domain).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getUserServiceAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(srcMachine.getIsSensitive() == null ? false : srcMachine.getIsSensitive()).append(SEPARATOR)
				.append(failureCode).append(SEPARATOR)
				.append(failureCodeScore).append(SEPARATOR)
				.append(clientAddress).append(SEPARATOR)
				.append(isNat).append(SEPARATOR)
				.append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
				.append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(srcClass).append(SEPARATOR)
				.append(dstMachine).append(SEPARATOR)
				.append(dstMachine.toUpperCase()).append(SEPARATOR)
				.append(normalizedDstMachineScore).append(SEPARATOR)
				.append(dstClass).append(SEPARATOR)
				.append(serviceId).append(SEPARATOR)
				.append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
				.append(eventScore).append(SEPARATOR)
				.append(timestamp).append(SEPARATOR)
				.append(HDFS_FOLDER_FORMAT.print(dt)).append(SEPARATOR);
		return sb.toString();
	}

	/**
	 *
	 * This method creates the actual csv line to write in HDFS for ssh
	 *
	 * @param dt
	 * @param user
	 * @param srcMachine
	 * @param dstMachine
	 * @param score
	 * @param reason
	 * @param clientAddress
	 * @param status
	 * @param authMethod
	 * @return
	 */
	public String buildSshHDFSLine(DateTime dt, User user, Computer srcMachine, String dstMachine, int score,
			EventFailReason reason, String clientAddress, String status, String authMethod) {
		String srcClass = "Desktop";
		String dstClass = "Server";
		int dateTimeScore = 0;
		int authMethodScore = 0;
		int normalizedSrcMachineScore = 0;
		int normalizedDstMachineScore = 0;
		boolean isNat = false;
		switch (reason) {
			case TIME: dateTimeScore = score; break;
			case FAILURE: authMethodScore = score; break;
			case SOURCE: normalizedSrcMachineScore = score; break;
			case DEST: normalizedDstMachineScore = score; break;
		}
		int eventScore = score;
		long timestamp = new Date().getTime();
		StringBuilder sb = new StringBuilder().append(HDFS_TIMESTAMP_FORMAT.print(dt)).append(SEPARATOR)
				.append(dt.getMillis() / 1000).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(user.getUserServiceAccount()).append(SEPARATOR)
				.append(status).append(SEPARATOR)
				.append(authMethod).append(SEPARATOR)
				.append(authMethodScore).append(SEPARATOR)
				.append(clientAddress).append(SEPARATOR)
				.append(isNat).append(SEPARATOR)
				.append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
				.append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(dstMachine).append(SEPARATOR)
				.append(dstMachine.toUpperCase()).append(SEPARATOR)
				.append(normalizedDstMachineScore).append(SEPARATOR)
				.append(srcMachine.getIsSensitive() == null ? false : srcMachine.getIsSensitive()).append(SEPARATOR)
				.append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
				.append(eventScore).append(SEPARATOR)
				.append(srcClass).append(SEPARATOR)
				.append(dstClass).append(SEPARATOR)
				.append(timestamp).append(SEPARATOR)
				.append(HDFS_FOLDER_FORMAT.print(dt)).append(SEPARATOR);
		return sb.toString();
	}

	/**
	 *
	 * This method creates the actual csv line to write in HDFS for vpn
	 *
	 * @param dt
	 * @param user
	 * @param srcMachine
	 * @param localIp
	 * @param score
	 * @param reason
	 * @param country
	 * @param status
	 * @return
	 */
	public String buildVpnHDFSLine(DateTime dt, User user, Computer srcMachine, String localIp, int score,
			EventFailReason reason, String country, String status) {
		//TODO - check if this is not the other way around with localIp, check if should extract as well
		String sourceIp = generateRandomIPAddress();
		String region = "Blantyre";
		String countryCode = "MW";
		String city = "Blantyre";
		String ipUsage = "isp";
		String isp = "Mtlonline.mw";
		String username = user.getUsername().split("@")[0];
		int dateTimeScore = 0;
		int normalizedSrcMachineScore = 0;
		int countryScore = 0;
		switch (reason) {
			case TIME: dateTimeScore = score; break;
			case SOURCE: normalizedSrcMachineScore = score; break;
			case COUNTRY: countryScore = score; break;
		}
		int eventScore = score;
		long timestamp = new Date().getTime();
		StringBuilder sb = new StringBuilder().append(HDFS_TIMESTAMP_FORMAT.print(dt)).append(SEPARATOR)
				.append(dt.getMillis() / 1000).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(username).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(status).append(SEPARATOR)
				.append(sourceIp).append(SEPARATOR)
				.append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(localIp).append(SEPARATOR)
				.append(country).append(SEPARATOR)
				.append(countryScore).append(SEPARATOR)
				.append(countryCode).append(SEPARATOR)
				.append(region).append(SEPARATOR)
				.append(city).append(SEPARATOR)
				.append(isp).append(SEPARATOR)
				.append(ipUsage).append(SEPARATOR)
				.append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
				.append(eventScore).append(SEPARATOR)
				.append(timestamp).append(SEPARATOR)
				.append(HDFS_FOLDER_FORMAT.print(dt)).append(SEPARATOR);
		return sb.toString();
	}

	/**
	 *
	 * This method creates the actual csv line to write in HDFS for print log
	 *
	 * @param dt
	 * @param user
	 * @param srcMachine
	 * @param dstMachine
	 * @param score
	 * @param fileSize
	 * @param totalPages
	 * @param reason
	 * @param fileName
	 * @param status
	 * @return
	 */
	public String buildPrintLogHDFSLine(DateTime dt, User user, Computer srcMachine, String dstMachine, int score,
			int fileSize, int totalPages, EventFailReason reason, String fileName, String status) {
		int totalPagesScore = 0;
		int dateTimeScore = 0;
		int fileSizeScore = 0;
		int normalizedSrcMachineScore = 0;
		int normalizedDstMachineScore = 0;
		switch (reason) {
			case TIME: dateTimeScore = score; break;
			case TOTAL_PAGES: totalPagesScore = score; break;
			case FILE_SIZE: fileSizeScore = score; break;
			case SOURCE: normalizedSrcMachineScore = score; break;
			case DEST: normalizedDstMachineScore = score; break;
		}
		int eventScore = score;
		long timestamp = new Date().getTime();
		String srcIP = "";
		String normalizedSrcMachine = "";
		String srcClass = "";
		String srcUsageType = "";
		String targetIP = "";
		String normalizedDstMachine = "";
		String dstClass = "";
		String dstISP = "";
		String dstUsageType = "";
		boolean isFromVPN = false;
		StringBuilder sb = new StringBuilder()
				.append(HDFS_TIMESTAMP_FORMAT.print(dt)).append(SEPARATOR)
				.append(dt.getMillis() / 1000).append(SEPARATOR)
				.append(user.getUsername().split("@")[0]).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(srcIP).append(SEPARATOR)
				.append(srcMachine.getName()).append(SEPARATOR)
				.append(normalizedSrcMachine).append(SEPARATOR)
				.append(srcClass).append(SEPARATOR)
				.append(srcUsageType).append(SEPARATOR)
				.append(targetIP).append(SEPARATOR)
				.append(dstMachine).append(SEPARATOR)
				.append(normalizedDstMachine).append(SEPARATOR)
				.append(dstClass).append(SEPARATOR)
				.append(dstISP).append(SEPARATOR)
				.append(dstUsageType).append(SEPARATOR)
				.append(status).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(user.getUserServiceAccount()).append(SEPARATOR)
				.append(srcMachine.getIsSensitive() == null ? false : srcMachine.getIsSensitive()).append(SEPARATOR)
				.append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(eventScore).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(normalizedDstMachineScore).append(SEPARATOR)
				.append(fileSize).append(SEPARATOR)
				.append(fileName).append(SEPARATOR)
				.append(totalPages).append(SEPARATOR)
				.append(fileSizeScore).append(SEPARATOR)
				.append(totalPagesScore).append(SEPARATOR)
				.append(isFromVPN).append(SEPARATOR)
				.append(timestamp).append(SEPARATOR)
				.append(HDFS_FOLDER_FORMAT.print(dt)).append(SEPARATOR);
		return sb.toString();
	}

	/**
	 *
	 * This method creates the actual csv line to write in HDFS for Oracle
	 *
	 * @param dt
	 * @param user
	 * @param srcMachine
	 * @param dstMachine
	 * @param score
	 * @param dbObject
	 * @param dbId
	 * @param reason
	 * @param dbUsername
	 * @param returnCode
	 * @param actionType
	 * @return
	 */
	public String buildOracleHDFSLine(DateTime dt, User user, Computer srcMachine, String dstMachine, int score,
			String dbObject, String dbId, EventFailReason reason, String dbUsername, String returnCode,
			String actionType) {
		int dbUsernameScore = 0;
		int actionTypeScore = 0;
		int dateTimeScore = 0;
		int dbObjectScore = 0;
		int normalizedSrcMachineScore = 0;
		int normalizedDstMachineScore = 0;
		switch (reason) {
			case TIME: dateTimeScore = score; break;
			case ACTION_TYPE: actionTypeScore = score; break;
			case USERNAME: dbUsernameScore = score; break;
			case OBJECT: dbObjectScore = score; break;
			case SOURCE: normalizedSrcMachineScore = score; break;
			case DEST: normalizedDstMachineScore = score; break;
		}
		int eventScore = score;
		long timestamp = new Date().getTime();
		String sourceIP = "";
		String srcClass = "Unknown";
		String targetIP = "";
		String dstClass = "Unknown";
		String status = "";
		String privUsed = "";
		boolean isFromVPN = false;
		StringBuilder sb = new StringBuilder()
				.append(HDFS_TIMESTAMP_FORMAT.print(dt)).append(SEPARATOR)
				.append(dt.getMillis() / 1000).append(SEPARATOR)
				.append(user.getUsername().split("@")[0]).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(sourceIP).append(SEPARATOR)
				.append(srcMachine.getName()).append(SEPARATOR)
				.append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
				.append(srcClass).append(SEPARATOR)
				.append(targetIP).append(SEPARATOR)
				.append(dstMachine).append(SEPARATOR)
				.append(dstMachine.toUpperCase()).append(SEPARATOR)
				.append(dstClass).append(SEPARATOR)
				.append(status).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(user.getUserServiceAccount()).append(SEPARATOR)
				.append(srcMachine.getIsSensitive() == null ? false : srcMachine.getIsSensitive()).append(SEPARATOR)
				.append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(eventScore).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(normalizedDstMachineScore).append(SEPARATOR)
				.append(dbUsername).append(SEPARATOR)
				.append(dbId).append(SEPARATOR)
				.append(privUsed).append(SEPARATOR)
				.append(dbObject).append(SEPARATOR)
				.append(returnCode).append(SEPARATOR)
				.append(actionType).append(SEPARATOR)
				.append(dbUsernameScore).append(SEPARATOR)
				.append(dbObjectScore).append(SEPARATOR)
				.append(actionTypeScore).append(SEPARATOR)
				.append(isFromVPN).append(SEPARATOR)
				.append(timestamp).append(SEPARATOR)
				.append(HDFS_FOLDER_FORMAT.print(dt)).append(SEPARATOR);
		return sb.toString();
	}

	/**
	 *
	 * This method creates the actual csv line to write in HDFS for NTLM
	 *
	 * @param dt
	 * @param user
	 * @param srcMachine
	 * @param score
	 * @param reason
	 * @param failureCode
	 * @return
	 */
	public String buildNTLMHDFSLine(DateTime dt, User user, Computer srcMachine, int score, EventFailReason reason,
			String failureCode) {
		int failureCodeScore = 0;
		int dateTimeScore = 0;
		int normalizedSrcMachineScore = 0;
		switch (reason) {
			case TIME: dateTimeScore = score; break;
			case SOURCE: normalizedSrcMachineScore = score; break;
			case FAILURE: failureCodeScore = score; break;
		}
		int eventScore = score;
		long timestamp = new Date().getTime();
		String sourceIP = "";
		String normalizedSrcMachine = "";
		String srcClass = "";
		String usageType = "";
		String status = "";
		StringBuilder sb = new StringBuilder()
				.append(HDFS_TIMESTAMP_FORMAT.print(dt)).append(SEPARATOR)
				.append(dt.getMillis() / 1000).append(SEPARATOR)
				.append(user.getUsername().split("@")[0]).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(sourceIP).append(SEPARATOR)
				.append(srcMachine.getName()).append(SEPARATOR)
				.append(normalizedSrcMachine).append(SEPARATOR)
				.append(srcClass).append(SEPARATOR)
				.append(usageType).append(SEPARATOR)
				.append(status).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(user.getUserServiceAccount()).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(eventScore).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(failureCode).append(SEPARATOR)
				.append(failureCodeScore).append(SEPARATOR)
				.append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
				.append(srcMachine.getIsSensitive() == null ? false : srcMachine.getIsSensitive()).append(SEPARATOR)
				.append(timestamp).append(SEPARATOR)
				.append(HDFS_FOLDER_FORMAT.print(dt)).append(SEPARATOR);
		return sb.toString();
	}

	/**
	 *
	 * This method creates the actual csv line to write in HDFS for WAME
	 *
	 * @param dt
	 * @param user
	 * @param actionType
	 * @param score
	 * @param reason
	 * @param domain
	 * @param status
	 * @param targetUsername
	 * @return
	 */
	public String buildWAMEHDFSLine(DateTime dt, User user, String actionType, int score, EventFailReason reason,
			String domain, String status, String targetUsername) {
		int actionTypeScore = 0;
		int dateTimeScore = 0;
		switch (reason) {
			case TIME: dateTimeScore = score; break;
			case ACTION_TYPE: actionTypeScore = score; break;
		}
		int eventScore = score;
		long timestamp = new Date().getTime();
		String sourceIP = "";
		String hostname = "";
		String normalizedSrcMachine = "";
		String srcClass = "";
		String usageType = "";
		boolean isSensitiveMachine = false;
		int normalizedSrcMachineScore = 0;
		String targetNormalizedUsername = "";
		StringBuilder sb = new StringBuilder()
				.append(HDFS_TIMESTAMP_FORMAT.print(dt)).append(SEPARATOR)
				.append(dt.getMillis() / 1000).append(SEPARATOR)
				.append(user.getUsername().split("@")[0]).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(sourceIP).append(SEPARATOR)
				.append(hostname).append(SEPARATOR)
				.append(normalizedSrcMachine).append(SEPARATOR)
				.append(srcClass).append(SEPARATOR)
				.append(usageType).append(SEPARATOR)
				.append(status).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(user.getUserServiceAccount()).append(SEPARATOR)
				.append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
				.append(isSensitiveMachine).append(SEPARATOR)
				.append(actionType).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(eventScore).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(actionTypeScore).append(SEPARATOR)
				.append(domain.toUpperCase()).append(SEPARATOR)
				.append(targetUsername).append(SEPARATOR)
				.append(domain.toUpperCase()).append(SEPARATOR)
				.append(targetNormalizedUsername).append(SEPARATOR)
				.append(timestamp).append(SEPARATOR)
				.append(HDFS_FOLDER_FORMAT.print(dt)).append(SEPARATOR);
		return sb.toString();
	}

	/**
	 *
	 * This method creates the actual csv line to write in HDFS for Salesforce
	 *
	 * @param dt
	 * @param user
	 * @param clientAddress
	 * @param score
	 * @param actionType
	 * @param city
	 * @param reason
	 * @param country
	 * @param status
	 * @param loginType
	 * @param browser
	 * @param application
	 * @param platform
	 * @return
	 */
	public String buildSalesforceHDFSLine(DateTime dt, User user, String clientAddress, int score, String actionType,
			String city, EventFailReason reason, String country, String status, String loginType, String browser,
			String application, String platform) {
		int countryScore = 0;
		int dateTimeScore = 0;
		int actionTypeScore = 0;
		int normalizedSrcMachineScore = 0;
		int normalizedDstMachineScore = 0;
		int statusScore = 0;
		switch (reason) {
			case TIME: dateTimeScore = score; break;
			case SOURCE: normalizedSrcMachineScore = score; break;
			case DEST: normalizedDstMachineScore = score; break;
			case COUNTRY: countryScore = score; break;
			case STATUS: statusScore = score; break;
			case ACTION_TYPE: actionTypeScore = score; break;
		}
		int eventScore = score;
		long timestamp = new Date().getTime();
		String hostname = "";
		String normalizedSrcMachine = "";
		String srcClass = "";
		String longitude = "";
		String latitude = "";
		String countryISOCode = "";
		String region = "";
		String isp = "";
		String usageType = "";
		String targetIP = "";
		String targetMachine = "";
		String normalizedDstMachine = "";
		String dstClass = "";
		String dstCountry = "";
		String dstLongitude = "";
		String dstLatitude = "";
		String dstCountryISOCode = "";
		String dstRegion = "";
		String dstCity = "";
		String dstISP = "";
		String dstUsageType = "";
		String loginURL = "";
		boolean isFromVPN = false;
		boolean isSensitiveMachine = false;
		StringBuilder sb = new StringBuilder()
				.append(HDFS_TIMESTAMP_FORMAT.print(dt)).append(SEPARATOR)
				.append(dt.getMillis() / 1000).append(SEPARATOR)
				.append(user.getUsername().split("@")[0]).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(clientAddress).append(SEPARATOR)
				.append(hostname).append(SEPARATOR)
				.append(normalizedSrcMachine).append(SEPARATOR)
				.append(srcClass).append(SEPARATOR)
				.append(country).append(SEPARATOR)
				.append(longitude).append(SEPARATOR)
				.append(latitude).append(SEPARATOR)
				.append(countryISOCode).append(SEPARATOR)
				.append(region).append(SEPARATOR)
				.append(city).append(SEPARATOR)
				.append(isp).append(SEPARATOR)
				.append(usageType).append(SEPARATOR)
				.append(targetIP).append(SEPARATOR)
				.append(targetMachine).append(SEPARATOR)
				.append(normalizedDstMachine).append(SEPARATOR)
				.append(dstClass).append(SEPARATOR)
				.append(dstCountry).append(SEPARATOR)
				.append(dstLongitude).append(SEPARATOR)
				.append(dstLatitude).append(SEPARATOR)
				.append(dstCountryISOCode).append(SEPARATOR)
				.append(dstRegion).append(SEPARATOR)
				.append(dstCity).append(SEPARATOR)
				.append(dstISP).append(SEPARATOR)
				.append(dstUsageType).append(SEPARATOR)
				.append(actionType).append(SEPARATOR)
				.append(status).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(user.getUserServiceAccount()).append(SEPARATOR)
				.append(isSensitiveMachine).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(eventScore).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(countryScore).append(SEPARATOR)
				.append(normalizedDstMachineScore).append(SEPARATOR)
				.append(actionTypeScore).append(SEPARATOR)
				.append(statusScore).append(SEPARATOR)
				.append(loginType).append(SEPARATOR)
				.append(browser).append(SEPARATOR)
				.append(platform).append(SEPARATOR)
				.append(application).append(SEPARATOR)
				.append(loginURL).append(SEPARATOR)
				.append(isFromVPN).append(SEPARATOR)
				.append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
				.append(timestamp).append(SEPARATOR)
				.append(HDFS_FOLDER_FORMAT.print(dt)).append(SEPARATOR);
		return sb.toString();
	}

	/**
	 *
	 * This method is a helper method for the event generators
	 *
	 * @param dt
	 * @param medianHour
	 * @param dataSource
	 * @param user
	 * @param computer
	 * @param dstMachines
	 * @param computerDomain
	 * @param dc
	 * @param clientAddress
	 * @param score
	 * @param standardDeviation
	 * @param maxHourOfWork
	 * @param minHourOfWork
	 * @return
	 * @throws HdfsException
	 */
	public LineAux baseLineGeneratorAux(DateTime dt, int medianHour, DemoUtils.DataSource dataSource, User user,
			Computer computer, String[] dstMachines, String computerDomain, String dc, String clientAddress, int score,
			int standardDeviation, int maxHourOfWork, int minHourOfWork) throws HdfsException, IOException {
		Random random = new Random();
		DateTime dateTime = generateRandomTimeForDay(dt, standardDeviation, medianHour, maxHourOfWork,
				minHourOfWork);
		String dstMachine = dstMachines[random.nextInt(dstMachines.length)];
		String lineToWrite = null;
		switch (dataSource) {
		case kerberos_logins: lineToWrite = buildKerberosHDFSLine(dateTime, user, computer, dstMachine,
				score, DemoUtils.EventFailReason.NONE, computerDomain, dc, clientAddress, "0x0"); break;
		case ssh: lineToWrite = buildSshHDFSLine(dateTime, user, computer, dstMachine, score,
				DemoUtils.EventFailReason.NONE, clientAddress, "Accepted", "password"); break;
		case vpn: break; //TODO - implement
		}
		return new LineAux(lineToWrite, dateTime);
	}

	/**
	 *
	 * This method generates a number of random destination machines
	 *
	 * @param minNumberOfDestMachines
	 * @param maxNumberOfDestMachines
	 * @param machines
	 * @return
	 */
	public Set<String> generateRandomDestinationMachines(List<Computer> machines, int minNumberOfDestMachines,
			int maxNumberOfDestMachines) {
		Random random = new Random();
		Set<String> result = new HashSet();
		maxNumberOfDestMachines = Math.min(machines.size(), maxNumberOfDestMachines);
		minNumberOfDestMachines = Math.min(machines.size(), minNumberOfDestMachines);
		int numberOfDestinationMachines;
		if (maxNumberOfDestMachines == minNumberOfDestMachines) {
			numberOfDestinationMachines = maxNumberOfDestMachines;
		} else {
			numberOfDestinationMachines = random.nextInt(maxNumberOfDestMachines - minNumberOfDestMachines) +
					minNumberOfDestMachines;
		}
		while (result.size() < numberOfDestinationMachines) {
			int index = random.nextInt(machines.size());
			result.add(machines.get(index).getName());
		}
		return result;
	}

	/**
	 *
	 * This method generates a random hour for a specific day
	 *
	 * @param dt
	 * @param standardDeviation
	 * @param mean
	 * @param max
	 * @param min
	 * @return
	 */
	public DateTime generateRandomTimeForDay(DateTime dt, int standardDeviation, int mean, int max, int min) {
		Random random = new Random();
		//temp initialization
		int hour = -1;
		//while the randomized time is not between normal work hours
		while (hour < min || hour > max) {
			hour = (int)(random.nextGaussian() * standardDeviation + mean);
		}
		return dt.withHourOfDay(hour)
				.withMinuteOfHour(random.nextInt(60))
				.withSecondOfMinute(random.nextInt(60))
				.withMillisOfSecond(random.nextInt(1000));
	}

	/**
	 *
	 * This method generates a random time for an anomaly
	 *
	 * @param dt
	 * @param minHour
	 * @param maxHour
	 * @return
	 */
	public DateTime generateRandomTimeForAnomaly(DateTime dt, int minHour, int maxHour) {
		Random random = new Random();
		int hour;
		if (maxHour == minHour) {
			hour = maxHour;
		} else {
			hour = random.nextInt(maxHour - minHour) + minHour;
		}
		return dt.withHourOfDay(hour)
				.withMinuteOfHour(random.nextInt(60))
				.withSecondOfMinute(random.nextInt(60))
				.withMillisOfSecond(random.nextInt(1000));
	}

	/**
	 *
	 * This method generates a random IP address
	 *
	 * @return
	 */
	public String generateRandomIPAddress() {
		Random random = new Random();
		return random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256);
	}

	/**
	 *
	 * This method stores the events in Impala and forwards them to the Evidence topic
	 *
	 * @param user
	 * @param dataSourceProperties
	 * @param lines
	 * @param hdfsServices
	 * @param impalaJdbcTemplate
	 * @return
	 * @throws HdfsException
	 */
	public List<JSONObject> saveEvents(User user, DataSourceProperties dataSourceProperties,
			List<LineAux> lines, List<HdfsService> hdfsServices, JdbcOperations impalaJdbcTemplate) throws Exception {
		Collections.sort(lines);
		for (LineAux lineAux: lines) {
			for (HdfsService hdfsService: hdfsServices) {
				hdfsService.writeLineToHdfs(lineAux.getLineToWrite(), lineAux.getDateTime().getMillis());
			}
		}
		for (HdfsService hdfsService: hdfsServices) {
			hdfsService.close();
		}
		long startTime = lines.get(0).getDateTime().getMillis() / 1000;
		long endTime = lines.get(lines.size() - 1).getDateTime().getMillis() / 1000;
		List<JSONObject> records = convertRowsToJSON(dataSourceProperties, startTime, endTime,
				user.getUsername(), impalaJdbcTemplate);
		return records;
	}

	/**
	 *
	 * This method converts a line in HDFS to json
	 *
	 * @param dataSourceProperties
	 * @param startTime
	 * @param endTime
	 * @param username
	 * @param impalaJdbcTemplate
	 * @return
	 */
	public List<JSONObject> convertRowsToJSON(DataSourceProperties dataSourceProperties, long startTime, long endTime,
			String username, JdbcOperations impalaJdbcTemplate) {
		String[] fieldsName = ImpalaParser.getTableFieldNamesAsArray(dataSourceProperties.getFields());
		ImpalaQuery query = new ImpalaQuery();
		query.select("*").from(dataSourceProperties.getImpalaTable());
		query.andWhere(gte(DemoUtils.EPOCH_TIME_FIELD, Long.toString(startTime)));
		query.andWhere(lte(DemoUtils.EPOCH_TIME_FIELD, Long.toString(endTime)));
		query.andEqInQuote(DemoUtils.NORMALIZED_USERNAME, username);
		query.limitAndSort(new ImpalaPageRequest(1000000, new Sort(Sort.Direction.DESC, DemoUtils.EPOCH_TIME_FIELD)));
		List<Map<String, Object>> resultsMap =  impalaJdbcTemplate.query(query.toSQL(), new ColumnMapRowMapper());
		List<JSONObject> result = new ArrayList();
		for (Map<String, Object> row : resultsMap) {
			JSONObject json = new JSONObject();
			for (String fieldName : fieldsName) {
				Object val = row.get(fieldName.toLowerCase());
				if (val instanceof Timestamp) {
					json.put(fieldName, val.toString());
				} else {
					json.put(fieldName, val);
				}
				json.put(DemoUtils.DATA_SOURCE_FIELD, dataSourceProperties.getDataSource().name());
				json.put(DemoUtils.LAST_STATE_FIELD, "HDFSWriterStreamTask");
			}
			result.add(json);
		}
		return result;
	}

	/**
	 *
	 * This method creates the indicator and adds it to Mongo
	 *
	 * @param username
	 * @param evidenceType
	 * @param startTime
	 * @param endTime
	 * @param dataEntityId
	 * @param score
	 * @param anomalyTypeFieldName
	 * @param anomalyValue
	 * @param numberOfEvents
	 * @param evidenceTimeframe
	 * @param evidencesService
	 * @return
	 */
	public Evidence createIndicator(String username, EvidenceType evidenceType, Date startTime, Date endTime,
			String dataEntityId, Double score, String anomalyTypeFieldName, String anomalyValue, int numberOfEvents,
			EvidenceTimeframe evidenceTimeframe, EvidencesService evidencesService) {
		Evidence indicator = evidencesService.createTransientEvidence(EntityType.User, NORMALIZED_USERNAME, username,
				evidenceType, startTime, endTime, Arrays.asList(new String[] { dataEntityId }), score, anomalyValue,
				anomalyTypeFieldName, numberOfEvents, evidenceTimeframe);
		evidencesService.saveEvidenceInRepository(indicator);
		return indicator;
	}

	/**
	 *
	 * This method is a helper method for creating indicators
	 *
	 * @param evidenceType
	 * @param reason
	 * @param indicators
	 * @param user
	 * @param randomDate
	 * @param dataSource
	 * @param indicatorScore
	 * @param anomalyTypeFieldName
	 * @param timeframe
	 * @param numberOfAnomalies
	 * @param anomalyDate
	 * @param evidencesService
	 */
	public void indicatorCreationAux(EvidenceType evidenceType, EventFailReason reason, List<Evidence> indicators,
			User user, DateTime randomDate, DataSource dataSource, int indicatorScore, String anomalyTypeFieldName,
			int numberOfAnomalies, DateTime anomalyDate, String dstMachine,
			String srcMachine, EvidenceTimeframe timeframe, EvidencesService evidencesService) {
		if (evidenceType == EvidenceType.AnomalySingleEvent) {
			switch (reason) {
			case TIME: {
				DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.0");
				indicators.add(createIndicator(user.getUsername(), evidenceType,
						randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
						anomalyTypeFieldName, dateTimeFormatter.print(randomDate), 1, timeframe, evidencesService));
				break;
			}
			case DEST: indicators.add(createIndicator(user.getUsername(), evidenceType,
					randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
					anomalyTypeFieldName, dstMachine, 1, timeframe, evidencesService)); break;
			case SOURCE: indicators.add(createIndicator(user.getUsername(), evidenceType,
					randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
					anomalyTypeFieldName, srcMachine, 1, timeframe, evidencesService)); break;
			case FAILURE: indicators.add(createIndicator(user.getUsername(), evidenceType,
					randomDate.toDate(), randomDate.toDate(), dataSource.name(), indicatorScore + 0.0,
					anomalyTypeFieldName, ((double)numberOfAnomalies) + "", 1, timeframe,
					evidencesService)); break;
			}
		} else {
			DateTime endDate;
			if (timeframe == EvidenceTimeframe.Hourly) {
				randomDate = randomDate.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
				endDate = randomDate.plusHours(1);
			} else {
				randomDate = anomalyDate;
				endDate = randomDate.plusDays(1);
			}
			indicators.add(createIndicator(user.getUsername(), evidenceType, randomDate.toDate(),
					endDate.minusMillis(1).toDate(), dataSource.name(), indicatorScore + 0.0, anomalyTypeFieldName +
							"_" + timeframe.name().toLowerCase(), ((double) numberOfAnomalies) + "", numberOfAnomalies,
					timeframe, evidencesService));
		}
	}

	/**
	 *
	 * This method creates the alert and adds it to Mongo
	 *
	 * @param title
	 * @param startTime
	 * @param endTime
	 * @param user
	 * @param evidences
	 * @param roundScore
	 * @param severity
	 * @param alertsService
	 */
	public void createAlert(String title, long startTime, long endTime, User user, List<Evidence> evidences,
			int roundScore, Severity severity, AlertsService alertsService) {
		Alert alert = new Alert(title, startTime, endTime, EntityType.User, user.getUsername(), evidences,
				evidences.size(), roundScore, severity, AlertStatus.Open, AlertFeedback.None, "", user.getId());
		alertsService.add(alert);
	}

}