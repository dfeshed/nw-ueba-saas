package fortscale.streaming.service.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.topology.EventTopologyService;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.BDPService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import java.io.FileReader;
import java.util.List;
import java.util.Map;


public class KafkaEventTopologyService implements EventTopologyService, InitializingBean {
    private static final Logger logger = Logger.getLogger(KafkaEventTopologyService.class);
    private static final String EVENT_TOPOLOGY_JSON_NODE_NAME = "event_topology";
    private static final String ERROR_MSG_FAILED_TO_SEND_EVENT = "Failed to send event: %s";
    private static final String ERROR_MSG_NULL_EVENT = "Got a null event to send";
    private static final String TASK_RUNNING_MODE_NORMAL = "normal";
    private static final String TASK_RUNNING_MODE_BDP = "bdp";
    private static final String ERROR_MSG_NO_OUTPUT_TOPIC = "Failed to send message. No mathcing output topic. The message: %s.";
    private static final String ERROR_MSG_NULL_JOB_NAME = "Job name is not set. The job name must be set prior to calling sendEvent() method.";

    @Value("${fortscale.streaming.topology.event_topology_json:}")
    private String eventTopologyJsonFileName;

    private MessageCollector messageCollector;
    private String sendingJobName;
    private BDPService bdpService;
    private Map<String, Map<String, List<Map<String, Map<String, String>>>>> jobName2RunningStatesMap;


    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotBlank(eventTopologyJsonFileName)) {
            loadEventTopologyFromFile(eventTopologyJsonFileName);
        }
        bdpService=new BDPService();
    }

    String getTaskRunningMode() {
        if(bdpService.isBDPRunning()) {
            return TASK_RUNNING_MODE_BDP;
        } else {
            return TASK_RUNNING_MODE_NORMAL;
        }
    }

    @SuppressWarnings("unchecked")
	private void loadEventTopologyFromFile(String fileName) throws IllegalArgumentException {
        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(new FileReader(fileName));
            jobName2RunningStatesMap = (Map<String, Map<String, List<Map<String, Map<String, String>>>>>)jsonObj.get(EVENT_TOPOLOGY_JSON_NODE_NAME);
        } catch (Exception e) {
            String errorMsg = String.format("Failed to read json conf file %s", fileName);
            logger.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }

    @Override
    public void sendEvent(JSONObject event) throws Exception{
        if(event==null) {
            throw new IllegalArgumentException(ERROR_MSG_NULL_EVENT);
        }

        String topic = getOutputTopicForEvent(event);
        if(topic!=null) {
            if(!topic.isEmpty()) {
                sendEvent(event, topic);
                return;
            } else {
                // Empty topic means that we do not forward the event.
                return;
            }
        } else {
            throw new Exception(String.format(ERROR_MSG_NO_OUTPUT_TOPIC, event.toJSONString()));
        }
    }

    public String getOutputTopicForEvent(JSONObject event) throws Exception{
        Assert.notNull(event);
        List<Map<String, Map<String, String>>> priorityListOfEventFieldName2value2topic =  getEventFieldName2value2topicMap();
        if(priorityListOfEventFieldName2value2topic!=null) {
            for(Map<String, Map<String, String>> eventFieldName2value2topic: priorityListOfEventFieldName2value2topic) {
                for(Map.Entry<String, Map<String, String>> fieldName2value2topicEntry: eventFieldName2value2topic.entrySet()) {
                    String fieldName = fieldName2value2topicEntry.getKey();
                    Map<String, String> fieldValue2topicMap = fieldName2value2topicEntry.getValue();
                    String fieldValue = event.getAsString(fieldName);
                    if(fieldValue!=null) {
                        return fieldValue2topicMap.get(fieldValue);
                    }
                }
            }
        }

        return null;
    }


    private void sendEvent(JSONObject event, String outputTopic) throws Exception {
        Assert.notNull(event);
        Assert.notNull(messageCollector);
        Assert.hasText(outputTopic);

        try{
            ObjectMapper mapper = new ObjectMapper();
            messageCollector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), mapper.writeValueAsString(event)));
        } catch(Exception e){
            throw new KafkaPublisherException(String.format(ERROR_MSG_FAILED_TO_SEND_EVENT, event.toString()), e);
        }
    }

    private List<Map<String, Map<String, String>>> getEventFieldName2value2topicMap() throws Exception{
        if(sendingJobName==null) {
            throw new Exception(ERROR_MSG_NULL_JOB_NAME);
        }
        Map<String, List<Map<String, Map<String, String>>>> state2EventFieldName2value2topicMap = jobName2RunningStatesMap.get(sendingJobName);
        if (state2EventFieldName2value2topicMap != null) {
            return state2EventFieldName2value2topicMap.get(getTaskRunningMode());
        }
        return null;
    }

    public void setMessageCollector(MessageCollector messageCollector) {
        Assert.notNull(messageCollector);
        this.messageCollector = messageCollector;
    }

    public void setSendingJobName(String sendingJobName) {
        Assert.notNull(sendingJobName);
        Assert.isTrue(!sendingJobName.isEmpty());
        this.sendingJobName = sendingJobName;
    }

}
