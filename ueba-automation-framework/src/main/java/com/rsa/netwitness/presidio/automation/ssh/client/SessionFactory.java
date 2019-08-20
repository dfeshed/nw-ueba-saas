package com.rsa.netwitness.presidio.automation.ssh.client;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;


/**
 * This class is used to handle ssh Session inside the pool.
 *
 */
class SessionFactory extends BaseKeyedPoolableObjectFactory<ServerDetails, Session> {

    /**
     * This creates a Session if not already present in the pool.
     */
    @Override
    public Session makeObject(ServerDetails serverDetails) {
        Session session = null;
        try {
            JSch jschSSHChannel = new JSch();
            jschSSHChannel.setKnownHosts(serverDetails.knownHostsFileName);
            session = jschSSHChannel.getSession(serverDetails.user, serverDetails.host, serverDetails.port);
            session.setPassword(serverDetails.password);
            session.setConfig("StrictHostKeyChecking", serverDetails.strictHostKeyChecking);
            session.connect(serverDetails.timeOut);

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