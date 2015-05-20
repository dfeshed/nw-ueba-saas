package fortscale.collection.jobs.event.forward;

import com.cloudbees.syslog.sender.AbstractSyslogMessageSender;
import com.cloudbees.syslog.sender.TcpSyslogMessageSender;
import com.cloudbees.syslog.sender.UdpSyslogMessageSender;
import fortscale.collection.JobDataMapExtension;
import fortscale.monitor.JobProgressReporter;
import fortscale.services.dataqueries.querydto.*;
import fortscale.services.dataqueries.querygenerators.DataQueryRunner;
import fortscale.services.dataqueries.querygenerators.DataQueryRunnerFactory;
import fortscale.services.event.forward.ForwardConfiguration;
import fortscale.services.event.forward.ForwardConfigurationRepository;
import fortscale.services.event.forward.ForwardSingleConfiguration;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Job class to help build event process jobs from saved files into hadoop
 */
@DisallowConcurrentExecution
public class EventForwardJob implements Job {

	private static Logger logger = LoggerFactory.getLogger(EventForwardJob.class);

	// get common data from configuration
	@Value("${destination.splunk.host}")
	protected String hostName;
	@Value("${destination.splunk.port}")
	protected int port;
	@Value("${destination.splunk.protocol}")
	protected String protocol;
	@Value("${destination.splunk.appName}")
	protected String appName;
	@Value("${destination.splunk.use.ssl}")
	protected boolean useSsl;
	@Value("${splunk.forward.update.buffer.size:10}")
	protected int updateBufferSize;
	@Value("${splunk.forward.connection.timeout:30000}")
	protected int connectionTimeout;
	@Value("${timestamp.safe.barrier.diff:30000}")
	protected int timestampDiff;


	@Autowired
	protected DataQueryRunnerFactory dataQueryRunnerFactory;

	@Autowired
	protected JobDataMapExtension jobDataMapExtension;

	@Autowired
	protected ForwardConfigurationRepository forwardConfigurationRepository;

	@Autowired
	protected JobProgressReporter monitor;

	protected String dataEntityUpdateTimestampField;

	protected long currentTimestamp;

	protected String monitorId;

	protected ForwardConfiguration forwardConfiguration;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		// get the job group name to be used using monitoring
		String sourceName = context.getJobDetail().getKey().getGroup();
		String jobName = context.getJobDetail().getKey().getName();

		logger.info("{} {} job started", jobName, sourceName);
		//for each configuration we have 2 steps + extra step for reading job parameters


		String currentStep = null;
		try {
			// get parameters from job data map
			currentStep = "Get Job Parameters";
			getJobParameters(context, sourceName);

			int stepNumber = (forwardConfiguration.getConfList().size() * 3);
			monitorId = monitor.startJob(sourceName, jobName, 3, true);
			// run configuration queries and forward data to Splunk
			for (int i = 0; i < forwardConfiguration.getConfList().size(); i++) {
				ForwardSingleConfiguration forwardSingleConfiguration = forwardConfiguration.getConfList().get(i);
				if (forwardSingleConfiguration != null) {
					//make sure we send all old unsent messages
					forwardEvents(forwardSingleConfiguration, forwardSingleConfiguration.getCachedEvents());
					//only if we succeed in sending old unsent messages we continue to new messages - to prevent out of memory problems.
					if (forwardSingleConfiguration.getCachedEvents() == null || forwardSingleConfiguration.getCachedEvents().isEmpty()) {
						//forward configuration data in 2 cases:
						// 	1. continues configuration
						//	2. single run and this is the first run
						if (forwardSingleConfiguration.isContinues() || forwardSingleConfiguration.getRunNumber() == 0) {
							DataQueryDTO dataQueryDTO = forwardSingleConfiguration.getDataQueryDTO();

							currentStep = "Run Data Query";
							monitor.startStep(monitorId, currentStep, 2 + (i * 2));
							List<Map<String, Object>> resultsMap = runQuery(dataQueryDTO);
							monitor.finishStep(monitorId, currentStep);

							currentStep = "Create syslog messages";
							monitor.startStep(monitorId, currentStep, 3 + (i * 2));
							List<String> messages = parseMessages(resultsMap);

							updateConfiguration(forwardSingleConfiguration, messages);
							monitor.finishStep(monitorId, currentStep);

							currentStep = "Forward Events to Splunk";
							monitor.startStep(monitorId, currentStep, 4 + (i * 2));
							forwardEvents(forwardSingleConfiguration, messages);
							monitor.finishStep(monitorId, currentStep);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("unexpected error during event process job: " ,e);
			monitor.error(monitorId, currentStep, e.toString());
			throw new JobExecutionException(e);
		} finally {
			monitor.finishJob(monitorId);
			logger.info("{} {} job finished", jobName, sourceName);
		}
	}

	private List<Map<String, Object>> runQuery(DataQueryDTO dataQueryDTO) throws Exception{
		DataQueryRunner dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(dataQueryDTO);
		//adding the upper bound of the time range to the current timestamp
		setDataQueryFieldValue(dataQueryDTO, dataEntityUpdateTimestampField, ","+currentTimestamp,true);
		// Generates query
		String query = dataQueryRunner.generateQuery(dataQueryDTO);
		logger.info("Running the query: {}", query);
		// execute Query
		return dataQueryRunner.executeQuery(query);
	}

	private List<String> parseMessages(List<Map<String, Object>> resultsMap){
		List<String> messages = new ArrayList<String>();
		for (Map<String, Object> event : resultsMap){
			messages.add(createSyslogMessage(event));
		}
		return messages;
	}

	private void forwardEvents(ForwardSingleConfiguration forwardSingleConfiguration, List<String> messages){
		if(messages != null) {
			// Initialise sender
			AbstractSyslogMessageSender messageSender = createMessageSender();

			//send message and update forward progress as we go
			sendMessages(messageSender, messages, forwardSingleConfiguration);

			logger.info("Forward {} events", messages.size());
		}
	}

	private void sendMessages(AbstractSyslogMessageSender messageSender, List<String> messages, ForwardSingleConfiguration forwardSingleConfiguration){
			int sendMessages = 1;
			List<String> unsentMessages = new ArrayList<String>(messages);
			for (String message : messages) {
				try {
					messageSender.sendMessage(message);
					//if the succeed in sending the message delete it from the unsent messages array
					unsentMessages.remove(message);
				} catch (Exception e) {
					//in the case of failure in sending the Syslog message continue without deleting the message from the unsentMessages
				}
				// update mongoDB with unsent messages progress every updateBufferSize messages
				sendMessages = handleForwardProgress(forwardSingleConfiguration, unsentMessages, sendMessages);
			}
			// update mongoDB when finished sending messages.
			handleForwardProgress(forwardSingleConfiguration, unsentMessages, updateBufferSize);
	}

	private AbstractSyslogMessageSender createMessageSender(){
		AbstractSyslogMessageSender messageSender = null;
		if (protocol.equals("tcp")) {
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

	private String createSyslogMessage(Map<String, Object> event){
		StringBuilder message = new StringBuilder();
		for(Map.Entry<String,Object> entry : event.entrySet()){
			message.append(entry.getKey()+"="+entry.getValue()+";");
		}
		return message.toString();
	}




	/**
	 *
	 * Handling forward progress and consistent in MongoDB
	 *
	 */

	private int handleForwardProgress(ForwardSingleConfiguration forwardSingleConfiguration, List<String> unsentMessages, int sendMessages){
		if(sendMessages >= updateBufferSize){
			forwardSingleConfiguration.setCachedEvents(unsentMessages);
			forwardConfigurationRepository.save(forwardConfiguration);
			return 1;
		}
		else{
			return ++sendMessages;
		}
	}

	private void updateConfiguration(ForwardSingleConfiguration forwardSingleConfiguration, List<String> messages){
		long runNumber = forwardSingleConfiguration.getRunNumber();
		forwardSingleConfiguration.setRunNumber(runNumber + 1);
		updateForwardProgress(forwardSingleConfiguration);
		forwardSingleConfiguration.setCachedEvents(messages);
		forwardConfigurationRepository.save(forwardConfiguration);
	}

	private void updateForwardProgress(ForwardSingleConfiguration forwardSingleConfiguration) {
		//add one millisecond to the latest timestamp - so in the between condition we won't get the same events again
		String newUpdateTimestamp = String.valueOf(currentTimestamp+1);
		DataQueryDTO dataQueryDTO = forwardSingleConfiguration.getDataQueryDTO();
		setDataQueryFieldValue(dataQueryDTO, dataEntityUpdateTimestampField, newUpdateTimestamp,false);
	}


	/**
	 *
	 * set configuration values
	 *
	 */

	private void setDataQueryFieldValue(DataQueryDTO dataQueryDTO, String fieldName, String newValue, boolean append){
		for(Term term : dataQueryDTO.getConditions().getTerms()){
			if(term instanceof ConditionField){
				DataQueryField field = ((ConditionField) term).getField();
				if (field != null && field.getId() != null && field.getId().equals(fieldName)){
					if (append){
						newValue = ((ConditionField) term).getValue() + newValue;
					}
					((ConditionField) term).setValue(newValue);
				}
			}
		}
	}



	/**
	 *
	 * Init steps
	 *
	 */

	protected void getJobParameters(JobExecutionContext context, String sourceName) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		// get parameters values from the job data map
		String dataEntity = jobDataMapExtension.getJobDataMapStringValue(map, "dataEntity");
		String defaultFieldsString = jobDataMapExtension.getJobDataMapStringValue(map, "dataEntityDefaultFields");
		String dataEntityScoreField = jobDataMapExtension.getJobDataMapStringValue(map, "dataEntityScoreField");
		int dataEntityDefaultMinimalScore = jobDataMapExtension.getJobDataMapIntValue(map, "dataEntityDefaultMinimalScore");
		dataEntityUpdateTimestampField = jobDataMapExtension.getJobDataMapStringValue(map, "dataEntityUpdateTimestampField");
		long dataEntityDefaultStartUpdateTimestamp = jobDataMapExtension.getJobDataMapLongValue(map, "dataEntityDefaultStartUpdateTimestamp");
		currentTimestamp = new Date().getTime() - timestampDiff;

		forwardConfiguration = forwardConfigurationRepository.findByType(sourceName);
		if (forwardConfiguration == null) {
			DataQueryDTO dataQueryDTO = createDataQuery(dataEntity, defaultFieldsString, dataEntityScoreField,dataEntityDefaultMinimalScore, dataEntityDefaultStartUpdateTimestamp);
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
	 *
	 * Helper functions for building query parts
	 *
	 */

	private DataQueryDTO createDataQuery(String dataEntity, String defaultFieldsString, String dataEntityScoreField,int dataEntityDefaultMinimalScore, long dataEntityDefaultStartUpdateTimestamp){
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
		dataQueryDTO.setLimit(Integer.MAX_VALUE);
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
		Term timeTerm = createDefaultTimeTerm(dataEntityDefaultStartUpdateTimestamp);
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

	private Term createDefaultTimeTerm(long dataEntityDefaultStartUpdateTimestamp){
		ConditionField updateTimestampTerm = new ConditionField();
		updateTimestampTerm.setQueryOperator(QueryOperator.between);
		updateTimestampTerm.setValue(String.valueOf(dataEntityDefaultStartUpdateTimestamp));
		DataQueryField dataQueryUpdateTimestampField = new DataQueryField();
		dataQueryUpdateTimestampField.setId(dataEntityUpdateTimestampField);
		updateTimestampTerm.setField(dataQueryUpdateTimestampField);

		return updateTimestampTerm;
	}
}

