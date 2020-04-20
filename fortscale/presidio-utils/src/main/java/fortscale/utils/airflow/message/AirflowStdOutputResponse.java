package fortscale.utils.airflow.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AirflowStdOutputResponse {

    @JsonProperty("stderr")
    private String stderr;

    @JsonProperty("stdin")
    private String stdin;

    @JsonProperty("stdout")
    private String stdout;

    public AirflowStdOutputResponse() {
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public String getStdin() {
        return stdin;
    }

    public void setStdin(String stdin) {
        this.stdin = stdin;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }
}
