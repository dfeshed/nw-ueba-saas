package com.rsa.netwitness.presidio.automation.ssh.client;

import com.rsa.netwitness.presidio.automation.ssh.helper.ServerDetails;
import org.assertj.core.util.Lists;

import java.util.List;

public abstract class SshExecHelper extends SshCommandExecutorBuilder {

    private SshCommandExecutorBuilder sshBuilder;

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


    public SshResponse run(String command) {
         return sshBuilder.setCommand(command).build().execute();
    }

    public SshResponse run() {
        return sshBuilder.build().execute();
    }

    public SshResponse run(String command, String... args) {
        final String CMD = arrangeArgs(Lists.list(args), command);
        return sshBuilder.setCommand(CMD).build().execute();
    }


    private String arrangeArgs(List<String> args, String command){
        StringBuilder line= new StringBuilder();
        for (String arg : args) {
            line.append(" ").append(arg);
        }
        return command+" "+line;
    }
}
