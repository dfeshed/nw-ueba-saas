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
import net.minidev.json.JSONObject;
import org.apache.zookeeper.KeeperException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.JobExecutionException;
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

	public enum EventFailReason { TIME, FAILURE, SOURCE, DEST, COUNTRY, FILE_SIZE, TOTAL_PAGES, STATUS, ACTION_TYPE,
		USERNAME, OBJECT, AUTH, RETURN_CODE, NONE }

	public enum DataSource { kerberos_logins, ssh, vpn, ntlm, wame, prnlog, oracle, crmsf, active_directory }

	public enum AnomalyType {

		TIME("event_time"),
		FAILURE_CODE("failure_code"),
		DEST("destination_machine"),
		SOURCE("source_machine"),
		ACTION_TYPE("action_type"),
		TAG("tag");

		public String text;

		AnomalyType(String text) {
			this.text = text;
		}

	}

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
	public static final String COMPUTER_SUFFIX = "_PC";
	public static final String SERVER_SUFFIX = "_SRV";
	public static final String COMPUTER_DOMAIN = "FORTSCALE";
	public static final String DOMAIN = "somebigcompany.com";
	public static final String DC = "FS-DC-01$";
	public static final String DISTINCT_NUMBER_OF_PREFIX = "distinct_number_of_";
	public static final String NUMBER_OF_FAILED_PREFIX = "number_of_failed_";
	public static final String NUMBER_OF_EVENTS_PREFIX = "number_of_events_";
	public static final String NUMBER_OF_SUCCESSFUL_PREFIX = "number_of_successful_";
	public static final String DST_MACHINES_SUFFIX = "_dst_machines_";
	public static final String NUMBER_OF_EVENTS_SUFFIX = "_events_";
	public static final int SLEEP_TIME = 1000 * 60 * 45;
	public static final int DEFAULT_SCORE = 0;
	public static final int DEFAULT_TAG_SCORE = 50;

	/**
	 *
	 * This method creates the actual csv line to write in HDFS for kerberos
	 *
	 * @param configuration
	 * @return
	 */
	public String buildKerberosHDFSLine(DemoGenericEvent configuration, DateTime dt) {
		DemoKerberosEvent event = (DemoKerberosEvent)configuration.generateEvent();
		String dstClass = ComputerUsageType.Server.name();
		int dateTimeScore = 0;
		int failureCodeScore = 0;
		int normalizedSrcMachineScore = 0;
		int normalizedDstMachineScore = 0;
		int score = configuration.getScore();
		User user = configuration.getUser();
		String serviceId = COMPUTER_DOMAIN + "\\" + DC;
		Computer srcMachine = event.getSrcMachine();
		String srcClass = srcMachine.getUsageType().name();
		String dstMachine = event.getDstMachine();
		boolean isNat = false;
		switch (configuration.getReason()) {
			case TIME: dateTimeScore = score; break;
			case FAILURE: failureCodeScore = score; break;
			case SOURCE: normalizedSrcMachineScore = score; break;
			case DEST: normalizedDstMachineScore = score; break;
		}
		int eventScore = score;
		long timestamp = new Date().getTime();
		StringBuilder sb = new StringBuilder()
				.append(HDFS_TIMESTAMP_FORMAT.print(dt)).append(SEPARATOR)
				.append(dt.getMillis() / 1000).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(DOMAIN).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getUserServiceAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(srcMachine.getIsSensitive() == null ? false : srcMachine.getIsSensitive()).append(SEPARATOR)
				.append(event.getFailureCode()).append(SEPARATOR)
				.append(failureCodeScore).append(SEPARATOR)
				.append(event.getClientAddress()).append(SEPARATOR)
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
	 * @param configuration
	 * @return
	 */
	public String buildSshHDFSLine(DemoGenericEvent configuration, DateTime dt) {
		DemoSSHEvent event = (DemoSSHEvent)configuration.generateEvent();
		String dstClass = ComputerUsageType.Server.name();
		int dateTimeScore = 0;
		int authMethodScore = 0;
		int normalizedSrcMachineScore = 0;
		int normalizedDstMachineScore = 0;
		int score = configuration.getScore();
		boolean isNat = false;
		User user = configuration.getUser();
		Computer srcMachine = event.getSrcMachine();
		String srcClass = srcMachine.getUsageType().name();
		String dstMachine = event.getDstMachine();
		switch (configuration.getReason()) {
			case TIME: dateTimeScore = score; break;
			case AUTH: authMethodScore = score; break;
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
				.append(event.getStatus()).append(SEPARATOR)
				.append(event.getAuthMethod()).append(SEPARATOR)
				.append(authMethodScore).append(SEPARATOR)
				.append(event.getClientAddress()).append(SEPARATOR)
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
	 * @param configuration
	 * @return
	 */
	public String buildVpnHDFSLine(DemoGenericEvent configuration, DateTime dt) {
		DemoVPNEvent event = (DemoVPNEvent)configuration.generateEvent();
		int score = configuration.getScore();
		User user = configuration.getUser();
		Computer srcMachine = event.getSrcMachine();
		String username = user.getUsername().split("@")[0];
		int dateTimeScore = 0;
		int normalizedSrcMachineScore = 0;
		int countryScore = 0;
		switch (configuration.getReason()) {
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
				.append(event.getStatus()).append(SEPARATOR)
				.append(event.getSourceIp()).append(SEPARATOR)
				.append(srcMachine.getName().toUpperCase()).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(event.getClientAddress()).append(SEPARATOR)
				.append(event.getCountry()).append(SEPARATOR)
				.append(countryScore).append(SEPARATOR)
				.append(event.getCountryCode()).append(SEPARATOR)
				.append(event.getRegion()).append(SEPARATOR)
				.append(event.getCity()).append(SEPARATOR)
				.append(event.getIsp()).append(SEPARATOR)
				.append(event.getIpUsage()).append(SEPARATOR)
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
	 * @param configuration
	 * @return
	 */
	public String buildPrintLogHDFSLine(DemoGenericEvent configuration, DateTime dt) {
		DemoPrintLogEvent event = (DemoPrintLogEvent)configuration.generateEvent();
		int totalPagesScore = 0;
		int dateTimeScore = 0;
		int fileSizeScore = 0;
		int normalizedSrcMachineScore = 0;
		int normalizedDstMachineScore = 0;
		int score = configuration.getScore();
		User user = configuration.getUser();
		Computer srcMachine = event.getSrcMachine();
		String dstMachine = event.getDstMachine();
		switch (configuration.getReason()) {
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
				.append(event.getStatus()).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(user.getUserServiceAccount()).append(SEPARATOR)
				.append(srcMachine.getIsSensitive() == null ? false : srcMachine.getIsSensitive()).append(SEPARATOR)
				.append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(eventScore).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(normalizedDstMachineScore).append(SEPARATOR)
				.append(event.getFileSize()).append(SEPARATOR)
				.append(event.getFileName()).append(SEPARATOR)
				.append(event.getTotalPages()).append(SEPARATOR)
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
	 * @param configuration
	 * @return
	 */
	public String buildOracleHDFSLine(DemoGenericEvent configuration, DateTime dt) {
		DemoOracleEvent event = (DemoOracleEvent)configuration.generateEvent();
		int dbUsernameScore = 0;
		int actionTypeScore = 0;
		int dateTimeScore = 0;
		int dbObjectScore = 0;
		int normalizedSrcMachineScore = 0;
		int normalizedDstMachineScore = 0;
		int returnCodeScore = 0;
		int score = configuration.getScore();
		User user = configuration.getUser();
		Computer srcMachine = event.getSrcMachine();
		String dstMachine = event.getDstMachine();
		switch (configuration.getReason()) {
			case TIME: dateTimeScore = score; break;
			case ACTION_TYPE: actionTypeScore = score; break;
			case USERNAME: dbUsernameScore = score; break;
			case OBJECT: dbObjectScore = score; break;
			case SOURCE: normalizedSrcMachineScore = score; break;
			case DEST: normalizedDstMachineScore = score; break;
			case RETURN_CODE: returnCodeScore = score; break;
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
				.append(event.getDbUsername()).append(SEPARATOR)
				.append(event.getDbId()).append(SEPARATOR)
				.append(privUsed).append(SEPARATOR)
				.append(event.getDbObject()).append(SEPARATOR)
				.append(event.getReturnCode()).append(SEPARATOR)
				.append(event.getActionType()).append(SEPARATOR)
				.append(dbUsernameScore).append(SEPARATOR)
				.append(returnCodeScore).append(SEPARATOR)
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
	 * @param configuration
	 * @return
	 */
	public String buildNTLMHDFSLine(DemoGenericEvent configuration, DateTime dt) {
		DemoNTLMEvent event = (DemoNTLMEvent)configuration.generateEvent();
		int failureCodeScore = 0;
		int dateTimeScore = 0;
		int normalizedSrcMachineScore = 0;
		int score = configuration.getScore();
		User user = configuration.getUser();
		Computer srcMachine = event.getSrcMachine();
		switch (configuration.getReason()) {
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
				.append(event.getFailureCode()).append(SEPARATOR)
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
	 * @param configuration
	 * @return
	 */
	public String buildWAMEHDFSLine(DemoGenericEvent configuration, DateTime dt) {
		DemoWAMEEvent event = (DemoWAMEEvent)configuration.generateEvent();
		int actionTypeScore = 0;
		int dateTimeScore = 0;
		int score = configuration.getScore();
		User user = configuration.getUser();
		switch (configuration.getReason()) {
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
				.append(event.getStatus()).append(SEPARATOR)
				.append(user.getAdministratorAccount()).append(SEPARATOR)
				.append(user.getExecutiveAccount()).append(SEPARATOR)
				.append(user.getUserServiceAccount()).append(SEPARATOR)
				.append(user.getTags().contains(UserTagEnum.LR.getId())).append(SEPARATOR)
				.append(isSensitiveMachine).append(SEPARATOR)
				.append(event.getActionType()).append(SEPARATOR)
				.append(dateTimeScore).append(SEPARATOR)
				.append(eventScore).append(SEPARATOR)
				.append(normalizedSrcMachineScore).append(SEPARATOR)
				.append(actionTypeScore).append(SEPARATOR)
				.append(DOMAIN.toUpperCase()).append(SEPARATOR)
				.append(event.getTargetUsername()).append(SEPARATOR)
				.append(DOMAIN.toUpperCase()).append(SEPARATOR)
				.append(targetNormalizedUsername).append(SEPARATOR)
				.append(timestamp).append(SEPARATOR)
				.append(HDFS_FOLDER_FORMAT.print(dt)).append(SEPARATOR);
		return sb.toString();
	}

	/**
	 *
	 * This method creates the actual csv line to write in HDFS for Salesforce
	 *
	 * @param configuration
	 * @return
	 */
	public String buildSalesforceHDFSLine(DemoGenericEvent configuration, DateTime dt) {
		DemoSalesForceEvent event = (DemoSalesForceEvent)configuration.generateEvent();
		int countryScore = 0;
		int dateTimeScore = 0;
		int actionTypeScore = 0;
		int statusScore = 0;
		int score = configuration.getScore();
		User user = configuration.getUser();
		switch (configuration.getReason()) {
			case TIME: dateTimeScore = score; break;
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
		int normalizedSrcMachineScore = 0;
		int normalizedDstMachineScore = 0;
		StringBuilder sb = new StringBuilder()
				.append(HDFS_TIMESTAMP_FORMAT.print(dt)).append(SEPARATOR)
				.append(dt.getMillis() / 1000).append(SEPARATOR)
				.append(user.getUsername().split("@")[0]).append(SEPARATOR)
				.append(user.getUsername()).append(SEPARATOR)
				.append(event.getClientAddress()).append(SEPARATOR)
				.append(hostname).append(SEPARATOR)
				.append(normalizedSrcMachine).append(SEPARATOR)
				.append(srcClass).append(SEPARATOR)
				.append(event.getCountry()).append(SEPARATOR)
				.append(longitude).append(SEPARATOR)
				.append(latitude).append(SEPARATOR)
				.append(countryISOCode).append(SEPARATOR)
				.append(region).append(SEPARATOR)
				.append(event.getCity()).append(SEPARATOR)
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
				.append(event.getActionType()).append(SEPARATOR)
				.append(event.getStatus()).append(SEPARATOR)
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
				.append(event.getLoginType()).append(SEPARATOR)
				.append(event.getBrowser()).append(SEPARATOR)
				.append(event.getPlatform()).append(SEPARATOR)
				.append(event.getApplication()).append(SEPARATOR)
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
	 * @param configuration
	 * @param standardDeviation
	 * @param maxHourOfWork
	 * @param minHourOfWork
	 * @return
	 * @throws HdfsException
	 */
	public DemoEvent baseLineGeneratorAux(DateTime dt, DemoGenericEvent configuration, int medianHour,
			DemoUtils.DataSource dataSource, int standardDeviation, int maxHourOfWork, int minHourOfWork)
			throws HdfsException, IOException, org.quartz.JobExecutionException {
		DateTime dateTime = generateRandomTimeForDay(dt, standardDeviation, medianHour, maxHourOfWork,
				minHourOfWork);
		String lineToWrite;
		lineToWrite = generateEvent(configuration, dataSource, dateTime);
		return new DemoEvent(lineToWrite, dateTime);
	}

	/**
	 *
	 * This method generates an event from a datasource and configuration
	 *
	 * @param configuration
	 * @param dataSource
	 * @param anomalyDate
	 * @param minHour
	 * @param maxHour
	 * @return
	 * @throws JobExecutionException
	 */
	public DemoEvent generateEvent(DemoGenericEvent configuration, DataSource dataSource, DateTime anomalyDate,
			int minHour, int maxHour)
			throws JobExecutionException {
		DateTime dt = generateRandomTimeForAnomaly(anomalyDate, minHour, maxHour);
		return new DemoEvent(generateEvent(configuration, dataSource, dt), dt);
	}

	/**
	 *
	 * This method generates an event from a datasource and configuration
	 *
	 * @param configuration
	 * @param dataSource
	 * @param dt
	 * @return
	 * @throws JobExecutionException
	 */
	public String generateEvent(DemoGenericEvent configuration, DataSource dataSource, DateTime dt)
			throws JobExecutionException {
		String lineToWrite;
		switch (dataSource) {
			case kerberos_logins: lineToWrite = buildKerberosHDFSLine((DemoKerberosEvent)configuration, dt);
				break;
			case ssh: lineToWrite = buildSshHDFSLine(configuration, dt); break;
			case vpn: lineToWrite = buildVpnHDFSLine(configuration, dt); break;
			case ntlm: lineToWrite = buildNTLMHDFSLine(configuration, dt); break;
			case wame: lineToWrite = buildWAMEHDFSLine(configuration, dt); break;
			case prnlog: lineToWrite = buildPrintLogHDFSLine(configuration, dt);	break;
			case oracle: lineToWrite = buildOracleHDFSLine(configuration, dt); break;
			case crmsf: lineToWrite = buildSalesforceHDFSLine(configuration, dt); break;
			default: throw new JobExecutionException();
		}
		return lineToWrite;
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
	public static String generateRandomIPAddress() {
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
			List<DemoEvent> lines, List<HdfsService> hdfsServices, JdbcOperations impalaJdbcTemplate) throws Exception {
		Collections.sort(lines);
		for (DemoEvent demoEventAux : lines) {
			for (HdfsService hdfsService: hdfsServices) {
				hdfsService.writeLineToHdfs(demoEventAux.getLineToWrite(), demoEventAux.getDateTime().getMillis());
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
				evidenceType, startTime, endTime, Arrays.asList(new String[]{dataEntityId}), score, anomalyValue,
				anomalyTypeFieldName, numberOfEvents, evidenceTimeframe);
		evidencesService.saveEvidenceInRepository(indicator);
		return indicator;
	}

	/**
	 *
	 * This method is a helper method for creating indicators
	 *
	 * @param evidenceType
	 * @param configuration
	 * @param indicators
	 * @param randomDate
	 * @param dataSource
	 * @param indicatorScore
	 * @param anomalyTypeFieldName
	 * @param timeframe
	 * @param numberOfAnomalies
	 * @param anomalyDate
	 * @param evidencesService
	 */
	public void indicatorCreationAux(EvidenceType evidenceType, DemoGenericEvent configuration,
			 List<Evidence> indicators, DateTime randomDate, DataSource dataSource, int indicatorScore,
			 String anomalyTypeFieldName, int numberOfAnomalies, DateTime anomalyDate, EvidenceTimeframe timeframe,
			 EvidencesService evidencesService) {
		configuration = configuration.generateEvent();
		User user = configuration.getUser();
		if (evidenceType == EvidenceType.AnomalySingleEvent) {
			if (configuration.getReason() == EventFailReason.TIME) {
				DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.0");
				indicators.add(createIndicator(user.getUsername(), evidenceType, randomDate.toDate(),
						randomDate.toDate(), dataSource.name(), indicatorScore + 0.0, anomalyTypeFieldName,
						dateTimeFormatter.print(randomDate), 1, timeframe, evidencesService));
			} else {
				indicators.add(createIndicator(user.getUsername(), evidenceType, randomDate.toDate(),
						randomDate.toDate(), dataSource.name(), indicatorScore + 0.0, anomalyTypeFieldName,
						configuration.getAnomalyValue(), 1, timeframe, evidencesService));
			}
		} else if (evidenceType == EvidenceType.AnomalyAggregatedEvent) {
			DateTime endDate;
			if (timeframe == EvidenceTimeframe.Hourly) {
				randomDate = randomDate.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
				endDate = randomDate.plusHours(1);
			} else {
				randomDate = anomalyDate;
				endDate = randomDate.plusDays(1);
			}
			if (configuration.getReason() == EventFailReason.FILE_SIZE || configuration.getReason() ==
					EventFailReason.TOTAL_PAGES) {
				indicators.add(createIndicator(user.getUsername(), evidenceType, randomDate.toDate(),
						endDate.minusMillis(1).toDate(), dataSource.name(), indicatorScore + 0.0,
						anomalyTypeFieldName +
								"_" + timeframe.name().toLowerCase(), configuration.getAnomalyValue() + "",
						numberOfAnomalies, timeframe, evidencesService));
			} else {
				indicators.add(createIndicator(user.getUsername(), evidenceType, randomDate.toDate(),
						endDate.minusMillis(1).toDate(), dataSource.name(), indicatorScore + 0.0,
						anomalyTypeFieldName + "_" + timeframe.name().toLowerCase(), ((double)numberOfAnomalies) +
								"", numberOfAnomalies, timeframe, evidencesService));
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 *
	 * This method creates a tag indicator
	 *
	 * @param user
	 * @param anomalyValue
	 * @param indicators
	 * @param anomalyDate
	 * @param evidencesService
	 */
	public void createTagEvidence(User user, String anomalyValue, List<Evidence> indicators, DateTime anomalyDate,
			EvidencesService evidencesService) {
		DateTime endDate = anomalyDate.plusDays(1);
		indicators.add(createIndicator(user.getUsername(), EvidenceType.Tag, anomalyDate.toDate(),
				endDate.minusMillis(1).toDate(), DataSource.active_directory.name(), DEFAULT_TAG_SCORE + 0.0, "tag",
				anomalyValue, 0, null, evidencesService));
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
	 * @return
	 */
	public Alert createAlert(String title, long startTime, long endTime, User user, List<Evidence> evidences,
			int roundScore, Severity severity, AlertsService alertsService) {
		Alert alert = new Alert(title, startTime, endTime, EntityType.User, user.getUsername(), evidences,
				evidences.size(), roundScore, severity, AlertStatus.Open, AlertFeedback.None, "", user.getId());
		alertsService.add(alert);
		return alert;
	}

}