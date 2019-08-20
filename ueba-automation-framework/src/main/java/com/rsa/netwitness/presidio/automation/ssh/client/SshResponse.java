package com.rsa.netwitness.presidio.automation.ssh.client;

import java.util.List;

public class SshResponse {
    public final int exitCode;
    public final List<String> output;
    public final List<String> error;

    SshResponse(int exitCode, List<String> output, List<String> error) {
        this.exitCode = exitCode;
        this.output = output;
        this.error = error;
    }

}
