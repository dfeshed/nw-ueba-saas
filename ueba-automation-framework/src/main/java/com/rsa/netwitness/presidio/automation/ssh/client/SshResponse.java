package com.rsa.netwitness.presidio.automation.ssh.client;

import java.util.List;

public class SshResponse {
    public final int exitCode;
    public final List<String> output;

    SshResponse(int exitCode, List<String> output) {
        this.exitCode = exitCode;
        this.output = output;
    }
}
