package com.rsa.netwitness.presidio.automation.ssh.client;

import com.rsa.netwitness.presidio.automation.ssh.helper.ServerDetails;
import org.assertj.core.util.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class SshExecHelper extends SshCommandExecutorBuilder {

    private SshCommandExecutorBuilder sshBuilder;
    private long timeout = 1;
    private TimeUnit unit = TimeUnit.HOURS;

    protected SshExecHelper(ServerDetails serverDetails) {
        sshBuilder = new SshCommandExecutorBuilder().setServerDetails(serverDetails);
    }


    public SshExecHelper setCommand(String command) {
        sshBuilder.setCommand(command);
        return this;
    }

    public SshExecHelper setUserDir(String userDir) {
        sshBuilder.setUserDir(userDir);
        return this;
    }

    public SshExecHelper setVerboseTrue() {
        sshBuilder.setVerboseTrue();
        return this;
    }

    public SshExecHelper withTimeout(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }


    public SshResponse run(String command) {
         return execute(sshBuilder.setCommand(command).build());
    }

    public SshResponse run() {
        return execute(sshBuilder.build());
    }

    public SshResponse run(String command, String... args) {
        final String CMD = arrangeArgs(Lists.list(args), command);
        return execute(sshBuilder.setCommand(CMD).build());
    }

    private SshResponse execute(SshCommandExecutor executor) {
        SshResponse result = executor.execute(timeout, unit);
        this.timeout = 1;
        this.unit = TimeUnit.HOURS;
        return result;
    }

    private String arrangeArgs(List<String> args, String command){
        StringBuilder line= new StringBuilder();
        for (String arg : args) {
            line.append(" ").append(arg);
        }
        return command+" "+line;
    }
}
