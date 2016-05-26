package com.rsa.asoc.sa.ui.common.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.rsa.asoc.sa.ui.common.data.Request;
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
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A service to track WebSocket subscriptions and handle any cleanup necessary when the client
 * unsubscribes or closes the connection.
 *
 * @author Abram Thielke
 * @since 10.6.0
 */
@Service
public class DefaultWebSocketSubscriptionService implements WebSocketSubscriptionService, SmartApplicationListener {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4,
            new DaemonThreadFactory("WebSocket Subscription Service Daemon"));

    private static final Multimap<String, CleanupTask> TASKS =
            Multimaps.synchronizedMultimap(ArrayListMultimap.create());

    @Override
    public String submit(MessageHeaders headers, CompletableFuture<?> future) {
        return submit(headers, null, future);
    }

    @Override
    public String submit(MessageHeaders headers, Runnable runnable) {
        return submit(headers, null, runnable);
    }

    @Override
    public String submit(MessageHeaders headers, Request request, CompletableFuture<?> future) {
        String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);
        String taskId = submit(headers, request, () -> future.cancel(true));
        future.handle((ok, ex) -> {
            cleanupTask(sessionId, taskId);
            return ok;
        });
        return taskId;
    }

    @Override
    public String submit(MessageHeaders headers, Request request, Runnable runnable) {
        String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);
        String subscriptionId = SimpMessageHeaderAccessor.getSubscriptionId(headers);
        String requestId = request != null ? request.getId() : null;

        CleanupTask task = new CleanupTask(subscriptionId, requestId, runnable);
        TASKS.put(sessionId, task);
        return task.id;
    }

    @Override
    public void cleanupTask(MessageHeaders headers, String taskId) {
        String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);
        cleanupTask(sessionId, taskId);
    }

    @Override
    public void cleanupTask(String sessionId, String taskId) {
        executeTasks(sessionId, (someTask) -> Objects.equals(someTask.id, taskId));
    }

    @Override
    public void cleanupRequest(MessageHeaders headers, String requestId) {
        String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);
        cleanupRequest(sessionId, requestId);
    }

    @Override
    public void cleanupRequest(String sessionId, String requestId) {
        executeTasks(sessionId, (someTask) -> Objects.equals(someTask.requestId, requestId));
    }

    @Override
    public Collection<String> getTaskIdsForSession(String sessionId) {
        return TASKS.get(sessionId).stream().map(CleanupTask::getId).collect(Collectors.toList());
    }

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
    private static final class CleanupTask {
        private final String id;
        private final String subscriptionId;
        private final String requestId;
        private final Runnable runnable;

        private CleanupTask(String subscriptionId, String requestId, Runnable runnable) {
            this.id = UUID.randomUUID().toString();
            this.subscriptionId = subscriptionId;
            this.requestId = requestId;
            this.runnable = runnable;
        }

        public String getId() {
            return id;
        }
    }
}
