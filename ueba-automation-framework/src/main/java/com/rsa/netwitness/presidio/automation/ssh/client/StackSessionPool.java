package com.rsa.netwitness.presidio.automation.ssh.client;

import com.jcraft.jsch.Session;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPool;


/**
 * Pool controller. This class exposes the org.apache.commons.pool.KeyedObjectPool class.
 *
 */
public class StackSessionPool {

    private final int MAX_SESSIONS = 10;

    private KeyedObjectPool<ServerDetails, Session> pool;

    private static class SingletonHolder {
        public static final StackSessionPool INSTANCE = new StackSessionPool();
    }

    public static StackSessionPool getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private StackSessionPool()
    {
        startPool();
    }

    /**
     *
     * @return the org.apache.commons.pool.KeyedObjectPool class
     */
    public KeyedObjectPool<ServerDetails, Session> getPool() {
        return pool;
    }

    /**
     *
     * @return the org.apache.commons.pool.KeyedObjectPool class
     */
    public void startPool() {
        pool = new StackKeyedObjectPool<ServerDetails, Session>(new SessionFactory(), MAX_SESSIONS);
    }
}