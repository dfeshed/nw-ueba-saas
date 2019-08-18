package com.rsa.netwitness.presidio.automation.ssh;

import com.jcraft.jsch.JSchException;

/**
 * singleton wrapeer for {@link SSHManager}
 */
public enum SSHManagerSingleton {
    INSTANCE;
    private SSHManager sshManager;

    {
        try {
            sshManager = new SSHManager();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    public SSHManager getSshManager() {
        return sshManager;
    }
}
