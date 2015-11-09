package com.rsa.asoc.sa.ui.common.service;

import com.rsa.asoc.sa.ui.common.data.Request;
import org.springframework.messaging.MessageHeaders;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * A service to track WebSocket subscriptions and long running requests, and handle any cleanup necessary when
 * the client unsubscribes, disconnects or cancels the request.
 *
 * @author Abram Thielke
 * @since 10.6.0
 */
public interface WebSocketSubscriptionService {

    /**
     * Adds a {@link CompletableFuture} to be {@link CompletableFuture#cancel(boolean)} when the session
     * or connection is closed.
     *
     * If the future completes (successfully or exceptionally), the task will be removed from the queue
     * immediately.
     */
    String submit(MessageHeaders headers, CompletableFuture<?> future);

    /**
     * Adds a {@link Runnable} to be executed when the session or connection is closed.
     */
    String submit(MessageHeaders headers, Runnable runnable);

    /**
     * Adds a {@link CompletableFuture} to be {@link CompletableFuture#cancel(boolean)} when the session is closed,
     * the connection is closed, or the given {@link Request} is explicitly canceled.
     *
     * If the future completes (successfully or exceptionally), the task will be removed from the queue
     * immediately.
     */
    String submit(MessageHeaders headers, Request request, CompletableFuture<?> future);

    /**
     * Adds a {@link Runnable} to be executed when the session is closed, the connection is closed, or the
     * given {@link Request} is explicitly canceled.
     */
    String submit(MessageHeaders headers, Request request, Runnable runnable);

    /**
     * Runs the clean up associated the session (which is extracted from the {@link MessageHeaders}), and the
     * given task identifier.
     */
    void cleanupTask(MessageHeaders headers, String taskId);

    /**
     * Runs the clean up associated the session ID, and the given task identifier.
     */
    void cleanupTask(String sessionId, String taskId);

    /**
     * Runs all clean up associated the session (which is extracted from the {@link MessageHeaders}), and the
     * given request identifier.
     */
    void cleanupRequest(MessageHeaders headers, String requestId);

    /**
     * Runs all clean up associated the session ID, and the given request identifier.
     */
    void cleanupRequest(String sessionId, String requestId);

    /**
     * Returns all task identifiers associated with the given session ID.
     */
    Collection<String> getTaskIdsForSession(String sessionId);
}
