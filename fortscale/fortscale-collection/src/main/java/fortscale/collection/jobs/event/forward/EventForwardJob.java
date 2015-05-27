package fortscale.collection.jobs.event.forward;

import com.cloudbees.syslog.sender.AbstractSyslogMessageSender;
import com.cloudbees.syslog.sender.TcpSyslogMessageSender;
import com.cloudbees.syslog.sender.UdpSyslogMessageSender;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.services.dataqueries.querydto.*;
import fortscale.services.dataqueries.querygenerators.DataQueryRunner;
import fortscale.services.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.services.event.forward.ForwardConfiguration;
import fortscale.services.event.forward.ForwardConfigurationRepository;
import fortscale.services.event.forward.ForwardSingleConfiguration;
import fortscale.utils.TimestampUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Job class to help forward event saved in hadoop into syslog server
 *
 * Every time the process run it query (in pages) the data updated in impala since the last run (according to update timestamp column)
 * it also condition on the event time to get only data from the last week, in order to use the partitions
 * every bufferSize events the process updates it's offset in mongo collection to allow recovery in the case of failure (process/network/destination)
 * once the process is completed successfully the query is updated for the next run
 * beside time range there is also a condition on the event score
 * and will be easy to add new automatic conditions in the future (since the data query mechanism is used).
 *
 * beside there is a possibility to define other query to be run (also non continues) that will be run only once and forward historical data.
 * all the process will work exactly the same beside the fact the query time range won't be changed once the forward is done.
 *
 * it is possible to changed the query configuration in mongo (fields, conditions, ... ) without a need to re run the process, on it's next run, it will use the new configuration.
 */
@DisallowConcurrentExecution
public class EventForwardJob extends FortscaleJob {

	private static Logger logger = LoggerFactory.getLogger(EventForwardJob.class);

	//get syslog server info from configuration
	@Value("${destination.syslog.server.host}")
	protected String hostName;
	@Value("${destination.syslog.server.port}")
	protected int port;
	@Value("${destination.syslog.server.protocol}")
	protected String protocol;
	@Value("${destination.syslog.server.appName}")
	protected String appName;
	@Value("${destination.syslog.server.use.ssl}")
	protected boolean useSsl;

	private static final String TCP_PROTOCOL = "tcp";


	 // The format of the dates in the exported file
	@Value("${syslog.server.forward.date.format:MMM dd yyyy HH:mm:ss 'GMT'Z}")
	private String exportDateFormat;

	// Timezone offset in minutes
	@Value("${syslog.server.forward.time.zone:0}")
	private Integer timezoneOffsetMins;

	//syslog server - connection timeout in milliseconds
	@Value("${syslog.server.forward.connection.timeout:30000}")
	protected int connectionTimeout;

	//buffer size to holds events forwarded before updating mongo
	@Value("${syslog.server.forward.update.buffer.size:500}")
	protected int updateBufferSize;

	@Value("${syslog.server.forward.timestamp.safe.barrier.diff:30000}")
	protected int timestampDiff;

	//when forward to syslog fails - retries before stopping the process
	@Value("${syslog.server.forward.retries.number:3}")
	protected int retries;

	//sleep time between retries
	@Value("${syslog.server.forward.sleep.time.between.retries:30000}")
	protected int sleepTime;

	//time in milliseconds (default - week)
	//uses for performance - using the partitions (time range on the event time and not event write time)
	@Value("${syslog.server.forward.timestamp.range.back:604800}")
	protected int timestampRange;


	@Autowired
	protected DataQueryRunnerFactory dataQueryRunnerFactory;

	@Autowired
	protected ForwardConfigurationRepository forwardConfigurationRepository;

	protected String dataEntityUpdateTimestampField;

	protected String dataEntityTimestampField;

	protected long currentTimestamp;

	private SimpleDateFormat sdf;

	String sourceName;

	String jobName;

	protected ForwardConfiguration forwardConfiguration;

	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}

	@Override
	protected int getTotalNumOfSteps(){
		//for each configuration we have 3 steps ( querying data , parsing , forwarding)
		return (forwardConfiguration.getConfList().size() * 3);
	}

	@Override
	protected void runSteps() throws Exception {

		logger.info("{} {} job started", jobName, sourceName);


		for (int i = 0; i < forwardConfiguration.getConfList().size(); i++) {
			ForwardSingleConfiguration forwardSingleConfiguration = forwardConfiguration.getConfList().get(i);
			if (forwardSingleConfiguration != null) {
				//forward configuration data in 2 cases:
				// 	1. continues configuration
				//	2. single run and this is the first run
				if (forwardSingleConfiguration.isContinues() || forwardSingleConfiguration.getRunNumber() == 0) {
					//adding the upper bound of the time range to the current timestamp
					List<String> messages = new ArrayList<String>();
					boolean finishSuccessfully = true;
					int page = 0;
					while (page == 0 || (finishSuccessfully && !messages.isEmpty())) {
						startNewStep("Run Data Query - page " + page);
						List<Map<String, Object>> resultsMap = runQuery(forwardSingleConfiguration);
						finishStep();

						startNewStep("Create syslog messages - page " + page);
								messages = parseMessages(resultsMap);
						finishStep();

						startNewStep("Forward Events to Syslog server - page " + page);
						finishSuccessfully |= forwardEvents(forwardSingleConfiguration, messages);
						finishStep();
						page++;
					}
					if (finishSuccessfully) {
						int offset = getConfigurationOffset(forwardSingleConfiguration);
						logger.info("Forward finished successfully - forward {} events", offset);
						updateConfiguration(forwardSingleConfiguration);

					}
				}
			}
		}
		logger.info("{} {} job finished", jobName, sourceName);
	}



	/**
	 *
	 * Run Data Query
	 *
	 */

	private List<Map<String, Object>> runQuery(ForwardSingleConfiguration forwardSingleConfiguration) throws Exception{
		DataQueryDTO dataQueryDTO = forwardSingleConfiguration.getDataQueryDTO();
		DataQueryRunner dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(dataQueryDTO);
		//when running continues query for a new time range - update the time range
		if(forwardSingleConfiguration.isContinues() && dataQueryDTO.getOffset() == 0){
			setDataQueryTimeFieldValue(dataQueryDTO, currentTimestamp);
		}
		// Generates query
		String query = dataQueryRunner.generateQuery(dataQueryDTO);
		logger.info("Running the query: {}", query);
		// execute Query
		return dataQueryRunner.executeQuery(query);
	}



	/**
	 *
	 * Create syslog messages
	 *
	 */

	private List<String> parseMessages(List<Map<String, Object>> resultsMap){
		List<String> messages = new ArrayList<String>();
		for (Map<String, Object> event : resultsMap){
			messages.add(createSyslogMessage(event));
		}
		return messages;
	}

	private String createSyslogMessage(Map<String, Object> event){
		StringBuilder message = new StringBuilder();

		for(Map.Entry<String,Object> entry : event.entrySet()) {
			String value = getValueAsString(sdf, entry.getValue());
			message.append(entry.getKey() + "=" + value + ";");
		}
		return message.toString();
	}

	private String getValueAsString(SimpleDateFormat sdf, Object value) {
		String strValue;
		if (value==null || value.toString().equalsIgnoreCase("null")) {
			return "";
		} else if (value instanceof Date) {
			strValue = sdf.format(value);
		} else {
			strValue = value.toString().replace("\"","'");
		}
		return "\"" + strValue + "\"";
	}



	/**
	 *
	 * Forward Events to Syslog server
	 *
	 */

	private boolean forwardEvents(ForwardSingleConfiguration forwardSingleConfiguration, List<String> messages){
		boolean sendSucceed = true;
		if(messages != null) {
			// Initialise sender
			AbstractSyslogMessageSender messageSender = createMessageSender();

			//send message and update forward progress as we go
			sendSucceed = sendMessages(messageSender, messages, forwardSingleConfiguration);

		}
		return sendSucceed;
	}

	private boolean sendMessages(AbstractSyslogMessageSender messageSender, List<String> messages, ForwardSingleConfiguration forwardSingleConfiguration) {
		int sendMessages = 0;
		int bufferSendMessages = 1;
		int offset = getConfigurationOffset(forwardSingleConfiguration);
		for (String message : messages) {
			try {
				messageSender.sendMessage(message);
				//if the succeed in sending the message update the offset
				offset++;
				sendMessages++;
			} catch (Exception e) {
				boolean sendSucceed = retrySendMessages(messageSender, message);
				if (sendSucceed) {
					offset++;
					sendMessages++;
				} else {
					//in the case of failure in sending the Syslog message stop the process and save the current state
					handleForwardProgress(forwardSingleConfiguration, offset, updateBufferSize);
					logger.info("Forward encounter problems - forward {} events, and stopped on offset {}", sendMessages, offset);
					return false;
				}
			}
			// update mongoDB with new offset
			bufferSendMessages = handleForwardProgress(forwardSingleConfiguration, offset, bufferSendMessages);
		}
		// update mongoDB when finished sending messages.
		handleForwardProgress(forwardSingleConfiguration, offset, updateBufferSize);
		return true;
	}

	private boolean retrySendMessages(AbstractSyslogMessageSender messageSender, String message) {
		for (int i = 0; i < retries; i++) {
			try {
				Thread.sleep(sleepTime);
				messageSender.sendMessage(message);
				return true;
			} catch (Exception e) {}
		}
		return false;
	}

	private AbstractSyslogMessageSender createMessageSender(){
		AbstractSyslogMessageSender messageSender = null;
		if (protocol.equalsIgnoreCase(TCP_PROTOCOL)) {
			messageSender = new TcpSyslogMessageSender();
			((TcpSyslogMessageSender) messageSender).setSyslogServerHostname(hostName);
			((TcpSyslogMessageSender) messageSender).setSyslogServerPort(port);
			((TcpSyslogMessageSender) messageSender).setSocketConnectTimeoutInMillis(connectionTimeout);
			((TcpSyslogMessageSender) messageSender).setSsl(useSsl);
		}
		else {
			messageSender = new UdpSyslogMessageSender();
			((UdpSyslogMessageSender) messageSender).setSyslogServerHostname(hostName);
			((UdpSyslogMessageSender) messageSender).setSyslogServerPort(port);
		}
		return messageSender;
	}



	/**
	 *
	 * Handling forward progress and consistent in MongoDB
	 *
	 */

	private int handleForwardProgress(ForwardSingleConfiguration forwardSingleConfiguration, int offset, int sendMessages){
		if(sendMessages >= updateBufferSize){
			setDataQueryOffset(forwardSingleConfiguration,offset);
			forwardConfigurationRepository.save(forwardConfiguration);
			return 1;
		}
		else{
			return ++sendMessages;
		}
	}

	private void updateConfiguration(ForwardSingleConfiguration forwardSingleConfiguration){
		long runNumber = forwardSingleConfiguration.getRunNumber();
		forwardSingleConfiguration.setRunNumber(runNumber + 1);
		if(forwardSingleConfiguration.isContinues()) {
			setDataQueryTimeFieldValue(forwardSingleConfiguration.getDataQueryDTO(), null);
		}
		setDataQueryOffset(forwardSingleConfiguration, 0);
		forwardConfigurationRepository.save(forwardConfiguration);
	}



	/**
	 *
	 * set configuration values
	 *
	 */

	private void setDataQueryTimeFieldValue(DataQueryDTO dataQueryDTO, Long newValue){
		for(Term term : dataQueryDTO.getConditions().getTerms()){
			if(term instanceof ConditionField){
				DataQueryField field = ((ConditionField) term).getField();
				if (field != null && field.getId() != null && field.getId().equals(dataEntityUpdateTimestampField)){
					String updateValue = "";
					if (newValue == null){
						//when setting value for next run
						//add one millisecond to the latest timestamp - so in the between condition we won't get the same events again
						long timestampUpperLimit = Long.parseLong(((ConditionField) term).getValue().split(",")[1]);
						updateValue = String.valueOf(timestampUpperLimit+1);
					}
					else{
						//when setting value for current run
						//adding the current timestamp as an upper limit
						updateValue = ((ConditionField) term).getValue() + "," + newValue;

					}
					((ConditionField) term).setValue(updateValue);
				}
				else if (newValue != null) {
					if (field != null && field.getId() != null && field.getId().equals(dataEntityTimestampField)) {
						((ConditionField) term).setValue(getTimeRange());
					}
				}
			}
		}
	}

	private void setDataQueryOffset(ForwardSingleConfiguration forwardSingleConfiguration, int offset){
		forwardSingleConfiguration.getDataQueryDTO().setOffset(offset);
	}

	private int getConfigurationOffset(ForwardSingleConfiguration forwardSingleConfiguration){
		return forwardSingleConfiguration.getDataQueryDTO().getOffset();
	}



	/**
	 *
	 * Init steps
	 *
	 */

	@Override
	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {

		// get the job group name to be used using monitoring
		sourceName = context.getJobDetail().getKey().getGroup();
		jobName = context.getJobDetail().getKey().getName();

		JobDataMap map = context.getMergedJobDataMap();

		// get parameters values from the job data map
		String dataEntity = jobDataMapExtension.getJobDataMapStringValue(map, "dataEntity");
		String defaultFieldsString = jobDataMapExtension.getJobDataMapStringValue(map, "dataEntityDefaultFields");
		String dataEntityScoreField = jobDataMapExtension.getJobDataMapStringValue(map, "dataEntityScoreField");
		int dataEntityDefaultMinimalScore = jobDataMapExtension.getJobDataMapIntValue(map, "dataEntityDefaultMinimalScore");
		dataEntityUpdateTimestampField = jobDataMapExtension.getJobDataMapStringValue(map, "dataEntityUpdateTimestampField");
		dataEntityTimestampField = jobDataMapExtension.getJobDataMapStringValue(map, "dataEntityTimestampField");
		long dataEntityDefaultStartUpdateTimestamp = jobDataMapExtension.getJobDataMapLongValue(map, "dataEntityDefaultStartUpdateTimestamp");
		int dataEntityLimit = jobDataMapExtension.getJobDataMapIntValue(map, "dataEntityLimit");
		currentTimestamp = System.currentTimeMillis() - timestampDiff;

		createSimpleDateFormat();

		forwardConfiguration = forwardConfigurationRepository.findByType(sourceName);
		if (forwardConfiguration == null) {
			DataQueryDTO dataQueryDTO = createDataQuery(dataEntity, defaultFieldsString, dataEntityScoreField,dataEntityDefaultMinimalScore, dataEntityDefaultStartUpdateTimestamp, dataEntityLimit);
			createForwardConfiguration(sourceName,dataQueryDTO);
			forwardConfigurationRepository.save(forwardConfiguration);
		}
	}

	private void createForwardConfiguration(String sourceName,DataQueryDTO dataQueryDTO){
		forwardConfiguration = new ForwardConfiguration();
		forwardConfiguration.setType(sourceName);
		List<ForwardSingleConfiguration> forwardSingleConfigurationList = new ArrayList<ForwardSingleConfiguration>();
		ForwardSingleConfiguration forwardSingleConfiguration = new ForwardSingleConfiguration();
		forwardSingleConfiguration.setContinues(true);
		forwardSingleConfiguration.setDataQueryDTO(dataQueryDTO);
		forwardSingleConfiguration.setRunNumber(0);
		forwardSingleConfigurationList.add(forwardSingleConfiguration);
		forwardConfiguration.setConfList(forwardSingleConfigurationList);
	}

	/**
	 * Create date formatter according to locale and timezone
	 * @return the formatter
	 */
	private void createSimpleDateFormat() {
		sdf = new SimpleDateFormat(exportDateFormat);

		// calculate timezone according to offset in minutes
		int timezoneOffset = 0;
		int minutes = 0;
		if (timezoneOffsetMins != null) {
			timezoneOffset = timezoneOffsetMins / 60;
			minutes = Math.abs(timezoneOffsetMins % 60);
		}
		// set timezone
		String timeZone = String.format("GMT%s%02d:%02d", (timezoneOffset >= 0 ? "+" : ""), timezoneOffset, minutes);
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
	}


	/**
	 *
	 * Helper functions for building query parts
	 *
	 */

	private DataQueryDTO createDataQuery(String dataEntity, String defaultFieldsString, String dataEntityScoreField,int dataEntityDefaultMinimalScore, long dataEntityDefaultStartUpdateTimestamp, int dataEntityLimit){
		//entity to forward
		DataQueryDTO dataQueryDTO = new DataQueryDTO();
		String[] entities = { dataEntity };
		dataQueryDTO.setEntities(entities);

		//fields to forward - we don't necessarily want all the fields
		List<DataQueryField> fieldsList = createQueryFields(defaultFieldsString);
		dataQueryDTO.setFields(fieldsList);

		//conditions on event time and score - not overflowing the client
		ConditionTerm conditionTerm = createDataQueryConditions(dataEntityScoreField,dataEntityDefaultMinimalScore, dataEntityDefaultStartUpdateTimestamp);
		dataQueryDTO.setConditions(conditionTerm);

		//sort according to event times for continues forwarding
		List<QuerySort> querySortList = createQuerySort();
		dataQueryDTO.setSort(querySortList);

		//no limit when getting the data
		dataEntityLimit = (dataEntityLimit == -1) ? Integer.MAX_VALUE : dataEntityLimit;
		dataQueryDTO.setLimit(dataEntityLimit);
		return dataQueryDTO;
	}

	private List<DataQueryField> createQueryFields(String defaultFieldsString){
		List<DataQueryField> defaultFieldsList = new ArrayList<DataQueryField>();
		String[] defaultFields = defaultFieldsString.split(",");
		for (String field : defaultFields) {
			DataQueryField dataQueryField = new DataQueryField();
			dataQueryField.setId(field);
			defaultFieldsList.add(dataQueryField);
		}
		return defaultFieldsList;
	}

	private List<QuerySort> createQuerySort(){
		List<QuerySort> querySortList = new ArrayList<QuerySort>();
		QuerySort queryUpdateTimestampSort = new QuerySort();
		DataQueryField dataQueryUpdateTimestampSortField = new DataQueryField();
		dataQueryUpdateTimestampSortField.setId(dataEntityUpdateTimestampField);
		queryUpdateTimestampSort.setField(dataQueryUpdateTimestampSortField);
		querySortList.add(queryUpdateTimestampSort);
		return querySortList;
	}

	private ConditionTerm createDataQueryConditions(String dataEntityScoreField,int dataEntityDefaultMinimalScore, long dataEntityDefaultStartUpdateTimestamp){
		ConditionTerm conditionTerm = new ConditionTerm();
		List<Term> terms = new ArrayList<Term>();
		Term scoreTerm = createDefaultScoreTerm(dataEntityScoreField, dataEntityDefaultMinimalScore);
		terms.add(scoreTerm);
		Term updateTimeTerm = createDefaultUpdateTimeTerm(dataEntityDefaultStartUpdateTimestamp);
		terms.add(updateTimeTerm);
		Term timeTerm = createDefaultTimeTerm();
		terms.add(timeTerm);
		conditionTerm.setTerms(terms);
		conditionTerm.setLogicalOperator(LogicalOperator.AND);
		return conditionTerm;
	}

	private Term createDefaultScoreTerm(String dataEntityScoreField,int dataEntityDefaultMinimalScore){
		ConditionField scoreTerm = new ConditionField();
		scoreTerm.setQueryOperator(QueryOperator.greaterThanOrEquals);
		scoreTerm.setValue(String.valueOf(dataEntityDefaultMinimalScore));
		DataQueryField dataQueryScoreField = new DataQueryField();
		dataQueryScoreField.setId(dataEntityScoreField);
		scoreTerm.setField(dataQueryScoreField);
		return scoreTerm;
	}

	private Term createDefaultUpdateTimeTerm(long dataEntityDefaultStartUpdateTimestamp){
		ConditionField updateTimestampTerm = new ConditionField();
		updateTimestampTerm.setQueryOperator(QueryOperator.between);
		updateTimestampTerm.setValue(String.valueOf(dataEntityDefaultStartUpdateTimestamp));
		DataQueryField dataQueryUpdateTimestampField = new DataQueryField();
		dataQueryUpdateTimestampField.setId(dataEntityUpdateTimestampField);
		updateTimestampTerm.setField(dataQueryUpdateTimestampField);

		return updateTimestampTerm;
	}

	private Term createDefaultTimeTerm(){
		ConditionField updateTimestampTerm = new ConditionField();
		updateTimestampTerm.setQueryOperator(QueryOperator.between);
		updateTimestampTerm.setValue(getTimeRange());
		DataQueryField dataQueryUpdateTimestampField = new DataQueryField();
		dataQueryUpdateTimestampField.setId(dataEntityTimestampField);
		updateTimestampTerm.setField(dataQueryUpdateTimestampField);

		return updateTimestampTerm;
	}


	private String getTimeRange(){
		return (TimestampUtils.convertToSeconds(currentTimestamp)-timestampRange)+","+ TimestampUtils.convertToSeconds(currentTimestamp);
	}
}

