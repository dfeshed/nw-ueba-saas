package com.rsa.netwitness.presidio.automation.ssh.client;

import static com.rsa.netwitness.presidio.automation.context.AutomationConf.*;

class ServerDetails {
    public String host;
    public String user;
    public String password;
    public int port;
    public String knownHostsFileName;
    public int timeOut;
    public String strictHostKeyChecking;

    ServerDetails(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.knownHostsFileName = "";
        this.port = 22;
        this.timeOut = 60000;
        this.strictHostKeyChecking = "no";
    }


    ServerDetails(String host, String user, String password, int port, String knownHostsFileName, int timeOut, String strictHostKeyChecking) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
        this.knownHostsFileName = knownHostsFileName;
        this.timeOut = timeOut;
        this.strictHostKeyChecking = strictHostKeyChecking;
    }

    static ServerDetails getUebaServer() {
        return new  ServerDetails(UEBA_HOST, SSH_USERNAME, SSH_PASSWORD);
    }

    static ServerDetails getUebaServerRoot() {
        return new  ServerDetails(UEBA_HOST, SSH_ROOT_USERNAME, SSH_ROOT_PASSWORD);
    }


    @Override
    public String toString() {
        return "ServerDetails{" +
                "host='" + host + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", port=" + port +
                ", knownHostsFileName='" + knownHostsFileName + '\'' +
                ", timeOut=" + timeOut +
                ", strictHostKeyChecking='" + strictHostKeyChecking + '\'' +
                '}';
    }

}
