package com.rsa.netwitness.presidio.automation.ssh.client;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;


/**
 * This class is used to handle ssh Session inside the pool.
 *
 */
public class SessionFactory extends BaseKeyedPoolableObjectFactory<ServerDetails, Session> {
    private final String knownHostsFileName = "";
    private final int port = 22;
    private final int timeOut = 60000;



    /**
     * This creates a Session if not already present in the pool.
     */
    @Override
    public Session makeObject(ServerDetails serverDetails) {
        Session session = null;
        try {
            JSch jschSSHChannel = new JSch();
            jschSSHChannel.setKnownHosts(knownHostsFileName);
            session = jschSSHChannel.getSession(serverDetails.getUser(), serverDetails.getHost(), serverDetails.getPort());
            session.setPassword(serverDetails.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(timeOut);

        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return session;
    }

    /**
     * This is called when closing the pool object
     */
    @Override
    public void destroyObject(ServerDetails serverDetails, Session session) {
        session.disconnect();
    }
}