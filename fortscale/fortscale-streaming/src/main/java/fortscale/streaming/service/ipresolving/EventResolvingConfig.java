package fortscale.streaming.service.ipresolving;

/**
 * Configuration for ip resolving on a specific event type. This should be constructed from the topology
 * settings or from the streaming task configuration and passed to the EventsIpResolvingService in order
 * to determine what action to take for each type of event.
 */
public class EventResolvingConfig {

    private String inputTopic;
    private String outputTopic;
    private String ipFieldName;
    private String hostFieldName;
    private String timestampFieldName;
    private boolean restrictToADName;
    private boolean shortName;
    private boolean isRemoveLastDot;

    /**
     * Builder for EventResolvingConfig, used as a utility function to simplify creation
     */
    public static EventResolvingConfig build(String inputTopic, String ipFieldName, String hostFieldName,
                                             String outputTopic, boolean restrictToADName, boolean shortName,
                                             boolean isRemoveLastDot, String timestampFieldName) {
        EventResolvingConfig config = new EventResolvingConfig();
        config.setHostFieldName(hostFieldName);
        config.setInputTopic(inputTopic);
        config.setIpFieldName(ipFieldName);
        config.setOutputTopic(outputTopic);
        config.setRestrictToADName(restrictToADName);
        config.setShortName(shortName);
        config.setRemoveLastDot(isRemoveLastDot);
        config.setTimestampFieldName(timestampFieldName);
        return config;
    }

    public String getInputTopic() {
        return inputTopic;
    }

    public void setInputTopic(String inputTopic) {
        this.inputTopic = inputTopic;
    }

    public String getOutputTopic() {
        return outputTopic;
    }

    public void setOutputTopic(String outputTopic) {
        this.outputTopic = outputTopic;
    }

    public String getIpFieldName() {
        return ipFieldName;
    }

    public void setIpFieldName(String ipFieldName) {
        this.ipFieldName = ipFieldName;
    }

    public String getHostFieldName() {
        return hostFieldName;
    }

    public void setHostFieldName(String hostFieldName) {
        this.hostFieldName = hostFieldName;
    }

    public String getTimestampFieldName() {
        return timestampFieldName;
    }

    public void setTimestampFieldName(String timestampFieldName) {
        this.timestampFieldName = timestampFieldName;
    }

    public boolean isRestrictToADName() {
        return restrictToADName;
    }

    public void setRestrictToADName(boolean restrictToADName) {
        this.restrictToADName = restrictToADName;
    }

    public boolean isShortName() {
        return shortName;
    }

    public void setShortName(boolean shortName) {
        this.shortName = shortName;
    }

    public boolean isRemoveLastDot() {
        return isRemoveLastDot;
    }

    public void setRemoveLastDot(boolean isRemoveLastDot) {
        this.isRemoveLastDot = isRemoveLastDot;
    }
}
