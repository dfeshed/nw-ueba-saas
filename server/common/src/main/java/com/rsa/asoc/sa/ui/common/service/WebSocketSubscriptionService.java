package com.rsa.asoc.sa.ui.common.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.rsa.netwitness.carlos.common.DaemonThreadFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

/**
 * A service to track websocket subscriptions and handle any cleanup necessary when the client
 * unsubscribes or closes the connection.
 *
 * @author Abram Thielke
 * @since 10.6.0
 */
@Service
public class WebSocketSubscriptionService implements SmartApplicationListener {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4,
            new DaemonThreadFactory("WebSocket Subscription Service Daemon"));

    private static final Multimap<String, CleanupTask> TASKS =
            Multimaps.synchronizedMultimap(ArrayListMultimap.create());

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return SessionUnsubscribeEvent.class.isAssignableFrom(eventType)
                || SessionDisconnectEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        MessageHeaders headers = ((AbstractSubProtocolEvent) event).getMessage().getHeaders();

        String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);
        String subscriptionId = SimpMessageHeaderAccessor.getSubscriptionId(headers);

        if (event instanceof SessionDisconnectEvent) {
            // This is a disconnect, so cleanup all the tasks for the session
            executeTasks(sessionId, (someTask) -> true);
        }
        else {
            // Just a subscription was closed
            executeTasks(sessionId, (someTask) -> subscriptionId.equals(someTask.subscriptionId));
        }
    }

    public CleanupTask submit(String sessionId, String subscriptionId, Runnable runnable) {
        CleanupTask task = new CleanupTask(sessionId, subscriptionId, runnable);
        TASKS.put(sessionId, task);
        return task;
    }

    public CleanupTask submit(MessageHeaders headers, Runnable runnable) {
        String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);
        String subscriptionId = SimpMessageHeaderAccessor.getSubscriptionId(headers);
        return submit(sessionId, subscriptionId, runnable);
    }

    public void cleanup(CleanupTask task) {
        executeTasks(task.sessionId, (someTask) -> task.id.equals(someTask.id));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private void executeTasks(String sessionId, Predicate<CleanupTask> predicate) {
        Collection<CleanupTask> tasks = TASKS.get(sessionId);
        synchronized (TASKS) {
            Iterator<CleanupTask> iterator = tasks.iterator();
            while (iterator.hasNext()) {
                CleanupTask task = iterator.next();
                if (predicate.test(task)) {
                    iterator.remove();
                    EXECUTOR.submit(task.runnable);
                }
            }
        }
    }

    /**
     * Clean up task
     */
    public static class CleanupTask {
        private final String id = UUID.randomUUID().toString();
        private final String sessionId;
        private final String subscriptionId;
        private final Runnable runnable;

        private CleanupTask(String sessionId, String subscriptionId, Runnable runnable) {
            this.sessionId = sessionId;
            this.subscriptionId = subscriptionId;
            this.runnable = runnable;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            CleanupTask that = (CleanupTask) object;

            if (!id.equals(that.id)) {
                return false;
            }
            else if (!runnable.equals(that.runnable)) {
                return false;
            }
            else if (!sessionId.equals(that.sessionId)) {
                return false;
            }
            return subscriptionId.equals(that.subscriptionId);

        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = 31 * result + sessionId.hashCode();
            result = 31 * result + subscriptionId.hashCode();
            result = 31 * result + runnable.hashCode();
            return result;
        }
    }
}
