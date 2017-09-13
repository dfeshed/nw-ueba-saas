package fortscale.utils.airflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 * Created by barak_schuster on 9/13/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AirflowApiResponse <T> implements Serializable{

    private Map arguments;
    @JsonProperty("post_arguments")
    private Map post_arguments;
    @JsonProperty("call_time")
    private Instant callTime;
    @JsonProperty("http_response_code")
    private int httpResponseCode;
    private T output;
    @JsonProperty("response_time")
    private Instant responseTime;
    private String status;

    public AirflowApiResponse() {
    }

    public Map getArguments() {
        return arguments;
    }

    public void setArguments(Map arguments) {
        this.arguments = arguments;
    }

    public Instant getCallTime() {
        return callTime;
    }

    public void setCallTime(Instant callTime) {
        this.callTime = callTime;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    public void setHttpResponseCode(int httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }

    public T getOutput() {
        return output;
    }

    public void setOutput(T output) {
        this.output = output;
    }

    public Instant getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Instant responseTime) {
        this.responseTime = responseTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map getPost_arguments() {
        return post_arguments;
    }

    public void setPost_arguments(Map post_arguments) {
        this.post_arguments = post_arguments;
    }
}
