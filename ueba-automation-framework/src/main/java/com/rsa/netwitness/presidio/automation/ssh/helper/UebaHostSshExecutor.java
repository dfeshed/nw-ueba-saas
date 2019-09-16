package com.rsa.netwitness.presidio.automation.ssh.helper;

import com.rsa.netwitness.presidio.automation.ssh.client.SshExecHelper;

class UebaHostSshExecutor extends SshExecHelper {

    UebaHostSshExecutor() {
        super(ServerDetails.getUebaServer());
    }
}
