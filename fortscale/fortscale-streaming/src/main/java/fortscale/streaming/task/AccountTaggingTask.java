package fortscale.streaming.task;

import fortscale.domain.core.ComputerUsageType;
import fortscale.services.UserService;
import fortscale.streaming.model.prevalance.PrevalanceModel;
import fortscale.streaming.model.tagging.AccountMachineAccess;
import fortscale.streaming.model.tagging.MachineState;
import fortscale.streaming.service.tagging.TagService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.utils.ConversionUtils.convertToBoolean;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Created by idanp on 7/7/2014.
 * Streaming task that get an event and made some heretics that tags the account related to that event
 */
public class AccountTaggingTask  implements StreamTask, InitableTask, WindowableTask, ClosableTask {

    private static final Logger logger = LoggerFactory.getLogger(EventsPrevalenceModelStreamTask.class);

    private TagService taggingService;

    private String usernameField;
    private String timestampField;
    private String sourceHostNameField;
    private String sourceComputerTypeField;
    private String destHostNameField;
    private String destComputerTypeField;
    private String isServiceAccountField;
    private String failureCodeField;
    private String isSensetiveMachineField;
    private String daysBackField;


    @SuppressWarnings("unchecked")
    @Override
    public void init(Config config, TaskContext context) throws Exception {

        usernameField = getConfigString(config, "fortscale.username.field");
        timestampField = getConfigString(config, "fortscale.timestamp.field");
        sourceHostNameField = config.get("fortscale.sourceHostName.field");
        sourceComputerTypeField = config.get("fortscale.sourceComputerType.field");
        destHostNameField = config.get("fortscale.destHostName.field");
        destComputerTypeField = config.get("fortscale.destComputerType.field");
        isServiceAccountField = config.get("fortscale.isServiceAccount.field");
        failureCodeField = config.get("fortscale.failureCode.field");
        isSensetiveMachineField= config.get("fortscale.isSensetiveMachine.field");
        daysBackField = config.get("fortscale.daysBack");

        String storeName = getConfigString(config, "fortscale.store.name");

        //get the store
        KeyValueStore<String, AccountMachineAccess> store = (KeyValueStore<String, AccountMachineAccess>)context.getStore(storeName);

        //validate that daysBackField is number
        Long dyasBack = convertToLong(daysBackField);

        if(dyasBack == null) {
            logger.error("the days back parameter at the proprieties file is invalid -  {}", daysBackField);
            return;
        }

        this.taggingService = new TagService(store,dyasBack);

    }


    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception
    {

        ComputerUsageType sourceComputerType;
        ComputerUsageType destComputerType;
        AccountMachineAccess currentAccount;
        boolean  isEventSuccess;


        // parse the message into json
        String messageText = (String)envelope.getMessage();
        JSONObject message = (JSONObject) JSONValue.parse(messageText);
        if (message==null) {
            logger.error("message in envelope cannot be parsed - {}", messageText);
            return;
        }

        // get the is service account flag
        // in case that there is other value then true or there is no value at all the isServiceAccount will get false
        boolean  isServiceAccount = convertToBoolean(message.get(isServiceAccountField));

        // get the failure code  and manipulate it for define the isEventSuccess flag
        String failureCode = convertToString(message.get(failureCodeField));
        if (StringUtils.isEmpty(failureCode) || failureCode == "0x0") {
            isEventSuccess = true;
        } else
            isEventSuccess = false;



        //Filter the non service accounts
        if(isServiceAccount && isEventSuccess) {

            // get the username
            String userName = convertToString(message.get(usernameField));
            if (StringUtils.isEmpty(userName)) {
                logger.error("message {} does not contains username in field {}", messageText, usernameField);
                return;
            }

            // get the timeStamp
            Long timeStamp = convertToLong(message.get(timestampField));
            if (timeStamp == null) {
                logger.error("message {} does not contains timeStamp in field {}", messageText, timestampField);
                return;
            }


            // get the source host name
            String sourceHostName = convertToString(message.get(sourceHostNameField));
            if (StringUtils.isEmpty(sourceHostName)) {
                logger.error("message {} does not contains sourceHostName in field {}", messageText, sourceHostNameField);
                return;
            }

            // get the source ComputerType
            try {
                sourceComputerType = ComputerUsageType.valueOf(convertToString(message.get(sourceComputerTypeField)));
            } catch (IllegalArgumentException ex) {
                logger.error("message {} does not contains valid source computer type in field {}", messageText, sourceComputerTypeField);
                return;
            }


            // get the destination host name
            String destHostName = convertToString(message.get(destHostNameField));
            if (StringUtils.isEmpty(destHostName)) {
                logger.error("message {} does not contains destHostName in field {}", messageText, destHostNameField);
                return;
            }


            // get the destination ComputerType
            try {
                destComputerType = ComputerUsageType.valueOf(convertToString(message.get(destComputerTypeField)));
            } catch (IllegalArgumentException ex) {
                logger.error("message {} does not contains valid destination computer type in field {}", messageText, destComputerTypeField);
                return;
            }


            // get the isSensetiveMachine  flag
            // in case that there is other value then true or there is no value at all the isSensetiveMachine will get false
            boolean isSensetiveMachine = convertToBoolean(message.get(isSensetiveMachineField));



            //handle the account
            this.taggingService.handleAccount(userName,timeStamp,sourceHostName,destHostName,sourceComputerType,destComputerType,isSensetiveMachine);


        }

    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator)
    {
        if(this.taggingService!=null)
            this.taggingService.exportTags();

    }

    @Override
    public void close() throws Exception {
        if(this.taggingService!=null)
            this.taggingService.exportTags();
        this.taggingService = null;

    }




}
