package com.rsa.netwitness.presidio.automation.ssh.client;

import ch.qos.logback.classic.Logger;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.rsa.netwitness.presidio.automation.ssh.helper.ServerDetails;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.slf4j.LoggerFactory;


/**
 * This class is used to handle ssh Session inside the pool.
 *
 */
class SessionFactory extends BaseKeyedPoolableObjectFactory<ServerDetails, Session> {
    static Logger LOGGER = (Logger) LoggerFactory.getLogger(SessionFactory.class);


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
            session.setConfig("PreferredAuthentications", "password");
            session.setPassword(serverDetails.password);
            session.setConfig("StrictHostKeyChecking", serverDetails.strictHostKeyChecking);
            session.connect(serverDetails.timeOut);
            if (session.isConnected()) {
                LOGGER.info("New ssh session is opened to " + serverDetails.user + "@" + serverDetails.host);
            } else {
                LOGGER.error("Failed to open ssh session to " + serverDetails.user + "@" + serverDetails.host);
                session.connect(serverDetails.timeOut);
                LOGGER.info("Session retry result: isConnected=" + session.isConnected());
            }

        } catch (Exception e) {
            LOGGER.error("Failed to open ssh session to " + serverDetails.user + ":" + serverDetails.password + "@" + serverDetails.host);
            LOGGER.error("Message: " + e.getMessage());
            e.printStackTrace();
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