package fortscale.utils.kafka.metricMessageModels;



import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "job-id",
        "samza-version",
        "job-name",
        "host",
        "reset-time",
        "container-name",
        "source",
        "time",
        "version"
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Header {

    @JsonProperty("job-id")
    private String jobId;
    @JsonProperty("samza-version")
    private String samzaVersion;
    @JsonProperty("job-name")
    private String jobName;
    @JsonProperty("host")
    private String host;
    @JsonProperty("reset-time")
    private long resetTime;
    @JsonProperty("container-name")
    private String containerName;
    @JsonProperty("source")
    private String source;
    @JsonProperty("time")
    private long time;
    @JsonProperty("version")
    private String version;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The jobId
     */
    @JsonProperty("job-id")
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId The job-id
     */
    @JsonProperty("job-id")
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return The samzaVersion
     */
    @JsonProperty("samza-version")
    public String getSamzaVersion() {
        return samzaVersion;
    }

    /**
     * @param samzaVersion The samza-version
     */
    @JsonProperty("samza-version")
    public void setSamzaVersion(String samzaVersion) {
        this.samzaVersion = samzaVersion;
    }

    /**
     * @return The jobName
     */
    @JsonProperty("job-name")
    public String getJobName() {
        return jobName;
    }

    /**
     * @param jobName The job-name
     */
    @JsonProperty("job-name")
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * @return The host
     */
    @JsonProperty("host")
    public String getHost() {
        return host;
    }

    /**
     * @param host The host
     */
    @JsonProperty("host")
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return The resetTime
     */
    @JsonProperty("reset-time")
    public long getResetTime() {
        return resetTime;
    }

    /**
     * @param resetTime The reset-time
     */
    @JsonProperty("reset-time")
    public void setResetTime(long resetTime) {
        this.resetTime = resetTime;
    }

    /**
     * @return The containerName
     */
    @JsonProperty("container-name")
    public String getContainerName() {
        return containerName;
    }

    /**
     * @param containerName The container-name
     */
    @JsonProperty("container-name")
    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    /**
     * @return The source
     */
    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    /**
     * @param source The source
     */
    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return The time
     */
    @JsonProperty("time")
    public long getTime() {
        return time;
    }

    /**
     * @param time The time
     */
    @JsonProperty("time")
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * @return The version
     */
    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    /**
     * @param version The version
     */
    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }
    public Header()
    {

    }
    @JsonCreator
    public Header(@JsonProperty("job-id") String jobId,@JsonProperty("samza-version") String samzaVersion,@JsonProperty("job-name") String jobName,@JsonProperty("host") String host,@JsonProperty("reset-time") long resetTime,@JsonProperty("container-name") String containerName,@JsonProperty("source") String source,@JsonProperty("time") long time,@JsonProperty("version") String version)
    {
        this.jobId=jobId;
        this.samzaVersion=samzaVersion;
        this.jobName=jobName;
        this.host=host;
        this.resetTime=resetTime;
        this.containerName=containerName;
        this.source=source;
        this.time=time;
        this.version=version;
    }
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}