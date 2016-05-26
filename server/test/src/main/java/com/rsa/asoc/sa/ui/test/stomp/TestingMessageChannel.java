package com.rsa.asoc.sa.ui.test.stomp;

import com.google.common.base.Preconditions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.AbstractSubscribableChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A {@link SubscribableChannel} that queues the messages to be sent in a list, instead of actually attempting
 * to deliver them.  Messages can be retrieved from the queue easily for tests.
 *
 * Optionally, add a {@link CountDownLatch} that will have its {@link CountDownLatch#countDown()} method called
 * for every message that gets "sent".
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public class TestingMessageChannel extends AbstractSubscribableChannel {

    private static final int DEFAULT_LATCH_WAIT_SECONDS = 5;

    private final List<Message<?>> messages = new ArrayList<>();
    private CountDownLatch countDownLatch;

    public TestingMessageChannel() {
    }

    public TestingMessageChannel(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    /**
     * Returns the underlying message queue.
     */
    public List<Message<?>> getMessages() {
        return messages;
    }

    /**
     * Removes the first message from the message queue.
     */
    public Message<?> popMessage() {
        Preconditions.checkState(!messages.isEmpty(), "No messages to retrieve");
        return messages.remove(0);
    }

    /**
     * Removes the first message from the message queue and casts it to the given type.
     */
    @SuppressWarnings("unchecked")
    public <T> Message<T> popMessage(ParameterizedTypeReference<T> type) {
        return (Message<T>) popMessage();
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    /**
     * Convenience method to call {@link CountDownLatch#await(long, TimeUnit)} with a default timeout.
     */
    public boolean await() throws InterruptedException {
        return countDownLatch == null || countDownLatch.await(DEFAULT_LATCH_WAIT_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    protected boolean sendInternal(Message<?> message, long timeout) {
        messages.add(message);
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
        return true;
    }
}
