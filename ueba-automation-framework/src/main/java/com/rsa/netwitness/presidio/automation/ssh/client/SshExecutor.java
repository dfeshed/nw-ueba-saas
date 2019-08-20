package com.rsa.netwitness.presidio.automation.ssh.client;

public class SshExecutor {
    private static SshCommandExecutor uebaHostExecutor = new SshCommandExecutor(ServerDetails.getUebaServer());
    private static SshCommandExecutor uebaHostExecutorRoot = new SshCommandExecutor(ServerDetails.getUebaServerRoot());

    public static SshResponse executeOnUebaHost(String command, boolean verbose, String userDir) {
        return uebaHostExecutor.execute(command, verbose, userDir);
    }

    public static SshResponse executeOnUebaHost(String command, boolean verbose) {
        return uebaHostExecutor.execute(command, verbose, "");
    }

    public static SshResponse executeOnUebaHost(String command) {
        return uebaHostExecutor.execute(command, false, "");
    }

    public static SshResponse executeOnUebaHostRoot(String command, boolean verbose, String userDir) {
        return uebaHostExecutorRoot.execute(command, verbose, userDir);
    }

    public static SshResponse executeOnUebaHostRoot(String command, boolean verbose) {
        return uebaHostExecutorRoot.execute(command, verbose, "");
    }

    public static SshResponse executeOnUebaHostRoot(String command) {
        return uebaHostExecutorRoot.execute(command, false, "");
    }
}
