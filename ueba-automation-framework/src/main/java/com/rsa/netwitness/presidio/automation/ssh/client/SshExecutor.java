package com.rsa.netwitness.presidio.automation.ssh.client;

public class SshExecutor {

    public static SshResponse executeOnUebaHost(String command, boolean verbose, String userDir) {
        return new SshCommandExecutor(ServerDetails.getUebaServer()).execute(command, verbose, userDir);
    }

    public static SshResponse executeOnUebaHost(String command, boolean verbose) {
        return new SshCommandExecutor(ServerDetails.getUebaServer()).execute(command, verbose, "");
    }

    public static SshResponse executeOnUebaHost(String command) {
        return new SshCommandExecutor(ServerDetails.getUebaServer()).execute(command, false, "");
    }

    public static SshResponse executeOnUebaHostRoot(String command, boolean verbose, String userDir) {
        return new SshCommandExecutor(ServerDetails.getUebaServerRoot()).execute(command, verbose, userDir);
    }

    public static SshResponse executeOnUebaHostRoot(String command, boolean verbose) {
        return new SshCommandExecutor(ServerDetails.getUebaServerRoot()).execute(command, verbose, "");
    }

    public static SshResponse executeOnUebaHostRoot(String command) {
        return new SshCommandExecutor(ServerDetails.getUebaServerRoot()).execute(command, false, "");
    }
}
