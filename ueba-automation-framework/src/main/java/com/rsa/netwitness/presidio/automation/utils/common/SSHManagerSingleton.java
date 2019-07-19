package com.rsa.netwitness.presidio.automation.utils.common;

import java.io.IOException;

/**
 * singleton wrapeer for {@link SSHManager}
 */
public enum  SSHManagerSingleton {
    INSTANCE;
    SSHManager sshManager;

    {
        try {
            sshManager = new SSHManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SSHManager getSshManager() {
        return sshManager;
    }
}
