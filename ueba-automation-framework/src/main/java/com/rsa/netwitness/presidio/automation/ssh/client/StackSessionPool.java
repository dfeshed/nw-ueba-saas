package com.rsa.netwitness.presidio.automation.ssh.client;

import com.jcraft.jsch.Session;
import com.rsa.netwitness.presidio.automation.ssh.helper.ServerDetails;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPool;


/**
 * Pool controller. This class exposes the org.apache.commons.pool.KeyedObjectPool class.
 *
 */
class StackSessionPool {

    private final int MAX_SESSIONS = 10;

    private KeyedObjectPool<ServerDetails, Session> pool;

    private static class SingletonHolder {
        public static final StackSessionPool INSTANCE = new StackSessionPool();
    }

    static StackSessionPool getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private StackSessionPool() {
        startPool();
    }

    /**
     *
     * @return the org.apache.commons.pool.KeyedObjectPool class
     */
    KeyedObjectPool<ServerDetails, Session> getPool() {
        return pool;
    }

    /**
     *
     * @return the org.apache.commons.pool.KeyedObjectPool class
     */
    void startPool() {
        pool = new StackKeyedObjectPool<>(new SessionFactory(), MAX_SESSIONS);
    }
}