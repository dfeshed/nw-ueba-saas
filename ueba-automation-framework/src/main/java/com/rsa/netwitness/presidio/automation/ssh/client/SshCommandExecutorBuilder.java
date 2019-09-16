package com.rsa.netwitness.presidio.automation.ssh.client;

import java.nio.file.Path;
import java.util.Objects;

class SshCommandExecutorBuilder {
    private ServerDetails serverDetails;
    private String command;
    private String userDir = "/var/netwitness/presidio/batch/";
    private boolean verbose = false;
    private Path logPath = null;

    SshCommandExecutorBuilder setServerDetails(ServerDetails serverDetails) {
        this.serverDetails = serverDetails;
        return this;
    }

    SshCommandExecutorBuilder setCommand(String command) {
        this.command = command;
        return this;
    }

    SshCommandExecutorBuilder setUserDir(String userDir) {
        this.userDir = userDir;
        return this;
    }

    SshCommandExecutorBuilder setVerboseTrue() {
        this.verbose = true;
        return this;
    }


    SshCommandExecutor build() {
        Objects.requireNonNull(serverDetails);
        Objects.requireNonNull(command);
        return new SshCommandExecutor(serverDetails, command, userDir, verbose);
    }
}