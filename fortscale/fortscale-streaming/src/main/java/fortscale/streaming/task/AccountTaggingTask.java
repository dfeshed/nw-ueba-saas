package fortscale.streaming.task;

import fortscale.domain.core.ComputerUsageType;
import fortscale.domain.core.Tag;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import fortscale.services.impl.SpringService;
import fortscale.services.impl.UserServiceImpl;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.service.tagging.TagService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.*;

/**
 * Created by idanp on 7/7/2014.
 * Streaming task that get an event and made some heretics that tags the account related to that event
 */
public class AccountTaggingTask extends AbstractStreamTask implements InitableTask, ClosableTask {

    private static final Logger logger = LoggerFactory.getLogger(AccountTaggingTask.class);

    private TagService taggingService;
	private UserService userService;

    private String usernameField;
    private String timestampField;
    private String sourceHostNameField;
    private String sourceComputerTypeField;
    private String destHostNameField;
    private String destComputerTypeField;
    private String failureCodeField;
    private List<String> failureCodes;
    private String isSensetiveMachineField;
    private String daysBackField;
    private Counter lastTimestampCount;


    @SuppressWarnings("unchecked")
    @Override
    protected void wrappedInit(Config config, TaskContext context) throws Exception {

        usernameField = getConfigString(config, "fortscale.username.field");
        timestampField = getConfigString(config, "fortscale.timestamp.field");
        sourceHostNameField = config.get("fortscale.sourceHostName.field");
        sourceComputerTypeField = config.get("fortscale.sourceComputerType.field");
        destHostNameField = config.get("fortscale.destHostName.field");
        destComputerTypeField = config.get("fortscale.destComputerType.field");
        failureCodeField = config.get("fortscale.failureCode.field");
        failureCodes = config.getList("fortscale.failureCodes");
        isSensetiveMachineField= config.get("fortscale.isSensetiveMachine.field");
        daysBackField = config.get("fortscale.daysBack");

        String storeName = getConfigString(config, "fortscale.store.name");

        //get the store
        KeyValueStore<String, AccountMachineAccess> store = (KeyValueStore<String, AccountMachineAccess>)context.getStore(storeName);

        //validate that daysBackField is number
        Long daysBack = convertToLong(daysBackField);

        if(daysBack == null) {
            logger.error("the days back parameter at the proprieties file is invalid -  {}", daysBackField);
            return;
        }

        this.taggingService = new TagService(store,daysBack);
		this.userService = SpringService.getInstance().resolve(UserService.class);

        // register metrics
        lastTimestampCount = context.getMetricsRegistry().newCounter(getClass().getName(), "account-tagging-epochime");
    }


    @Override
    public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception
    {

        ComputerUsageType sourceComputerType;
        ComputerUsageType destComputerType;
        boolean  isEventSuccess = false;


        // parse the message into json
        String messageText = (String)envelope.getMessage();
        JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);

        // get the failure code  and manipulate it for define the isEventSuccess flag
        String failureCode = convertToString(message.get(failureCodeField));


        if(failureCodes.contains(failureCode))
            isEventSuccess = true;



        //Filter the non service accounts
        if(isEventSuccess) {

            // get the username
            String userName = convertToString(message.get(usernameField));
            if (StringUtils.isEmpty(userName)) {
                //logger.error("message {} does not contains username in field {}", messageText, usernameField);
                throw new StreamMessageNotContainFieldException(messageText, usernameField);
            }

			// get the is service account flag
			// in case that there is other value then true or there is no value at all the isServiceAccount will get false
			boolean isServiceAccount = userService.isUserTagged(userName, Tag.SERVICE_ACCOUNT_TAG);

			if(isServiceAccount) {

				// get the timeStamp
				Long timeStamp = convertToLong(message.get(timestampField));
				if (timeStamp == null) {
					//logger.error("message {} does not contains timeStamp in field {}", messageText, timestampField);
					throw new StreamMessageNotContainFieldException(messageText, timestampField);
				}

				// get the source host name
				String sourceHostName = convertToString(message.get(sourceHostNameField));

				// get the source ComputerType
				try {
					String srcClassValue = convertToString(message.get(sourceComputerTypeField));
					sourceComputerType = StringUtils.isEmpty(srcClassValue) ? ComputerUsageType.Unknown : ComputerUsageType.valueOf(srcClassValue);
				} catch (IllegalArgumentException ex) {
					throw new StreamMessageNotContainFieldException(messageText, sourceComputerTypeField);
				}

				// get the destination host name
				String destHostName = convertToString(message.get(destHostNameField));

				// get the destination ComputerType
				try {
					String destClassValue = convertToString(message.get(destComputerTypeField));
					destComputerType = StringUtils.isEmpty(destClassValue) ? ComputerUsageType.Unknown : ComputerUsageType.valueOf(destClassValue);
				} catch (IllegalArgumentException ex) {
					throw new StreamMessageNotContainFieldException(messageText, destComputerTypeField);
				}

				// get the isSensetiveMachine  flag
				// in case that there is other value then true or there is no value at all the isSensetiveMachine will get false
				boolean isSensetiveMachine = message.get(isSensetiveMachineField) == null ? false : convertToBoolean(message.get(isSensetiveMachineField));

				//handle the account
				this.taggingService.handleAccount(userName, timeStamp, sourceHostName, destHostName, sourceComputerType, destComputerType, isSensetiveMachine);

				// update metric
				lastTimestampCount.set(timeStamp);
			}
        }

    }

    @Override
    public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception{
        if(this.taggingService!=null)
            this.taggingService.exportTags();

    }

    @Override
    protected void wrappedClose() throws Exception {
        if(this.taggingService!=null) {
            this.taggingService.exportTags();
        }
        this.taggingService = null;
    }




}
