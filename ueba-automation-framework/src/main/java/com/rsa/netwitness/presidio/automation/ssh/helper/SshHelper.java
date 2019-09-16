package com.rsa.netwitness.presidio.automation.ssh.helper;

import com.rsa.netwitness.presidio.automation.ssh.client.SshExecHelper;

public class SshHelper {

    public SshExecHelper uebaHostExec() {
        return new UebaHostSshExecutor();
    }

    public SshExecHelper uebaHostRootExec() {
        return new UebaHostSshExecutorRoot();
    }
}
