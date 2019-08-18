package com.rsa.netwitness.presidio.automation.ssh;

/**
 * singleton wrapeer for {@link SSHManager}
 */
public enum SSHManagerSingleton {
    INSTANCE;
    private SSHManager sshManager;

    {
        sshManager = new SSHManager();
    }

    public SSHManager getSshManager() {
        return sshManager;
    }
}
