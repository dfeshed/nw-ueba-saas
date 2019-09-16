package com.rsa.netwitness.presidio.automation.ssh.helper;

import com.rsa.netwitness.presidio.automation.ssh.client.SshExecHelper;

class UebaHostSshExecutorRoot extends SshExecHelper {

    UebaHostSshExecutorRoot() {
        super(ServerDetails.getUebaServerRoot());
    }
}
