package com.rsa.asoc.sa.ui.common.config;

import com.google.common.base.MoreObjects;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized settings for the global CARLOS transport and protocol-specific settings.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
@ConfigurationProperties(prefix = "carlos.transport")
public class CarlosTransportSettings {

    /**
     * NW Settings
     *
     * Attributes on this class are set using the "carlos.transport.nw." prefix.
     */
    private NwSettings nw = new NwSettings();

    /**
     * JMS Settings
     *
     * Attributes on this class are set using the "carlos.transport.jms." prefix.
     */
    private JmsSettings jms = new JmsSettings();

    /**
     * AMQP Settings
     *
     * Attributes on this class are set using the "carlos.transport.amqp." prefix.
     */
    private AmqpSettings amqp = new AmqpSettings();

    public NwSettings getNw() {
        return nw;
    }

    public void setNw(NwSettings nw) {
        this.nw = nw;
    }

    public JmsSettings getJms() {
        return jms;
    }

    public void setJms(JmsSettings jms) {
        this.jms = jms;
    }

    public AmqpSettings getAmqp() {
        return amqp;
    }

    public void setAmqp(AmqpSettings amqp) {
        this.amqp = amqp;
    }

    /**
     * Externalized NW Protocol settings.
     */
    public static class NwSettings {
        /**
         * A boolean indicating if this transport type should be configured.
         */
        private boolean enabled = true;

        /**
         * The connection timeout in milliseconds.
         */
        private int connectionTimeout = 30_000;

        /**
         * The socket timeout in milliseconds.
         */
        private int socketTimeout = 60_000;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public int getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("enabled", enabled)
                    .add("connectionTimeout", connectionTimeout)
                    .add("socketTimeout", socketTimeout)
                    .toString();
        }
    }

    /**
     * Externalized JMS/ActiveMQ settings.
     */
    public static class JmsSettings {
        /**
         * A boolean indicating if this transport type should be configured.
         */
        private boolean enabled = true;

        /**
         * The memory limit for the client in bytes.  Reaching this limit will prevent subsequent messages from
         * being accepted.
         */
        private long memoryLimit = 268_435_456;

        /**
         * Timeout to wait for space to be available on the broker in milliseconds.
         *
         * This property causes the send() operation to fail with an exception on the client-side, but only after
         * waiting the given amount of time. If space on the broker is still not freed after the configured amount
         * of time, only then does the send() operation fail with an exception to the client-side.
         */
        private long sendFailIfNoSpaceAfterTimeout = 300_000;

        /**
         * The number of messages to fetch from the queue at a time.
         *
         * The queue prefetch limit controls how many messages can be streamed to a consumer at any point in time.
         * Once the prefetch limit is reached, no more messages are dispatched to the consumer until the consumer
         * starts sending back acknowledgements of messages (to indicate that the message has been processed).
         */
        private int queuePrefetch = 1;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getMemoryLimit() {
            return memoryLimit;
        }

        public void setMemoryLimit(long memoryLimit) {
            this.memoryLimit = memoryLimit;
        }

        public long getSendFailIfNoSpaceAfterTimeout() {
            return sendFailIfNoSpaceAfterTimeout;
        }

        public void setSendFailIfNoSpaceAfterTimeout(long sendFailIfNoSpaceAfterTimeout) {
            this.sendFailIfNoSpaceAfterTimeout = sendFailIfNoSpaceAfterTimeout;
        }

        public int getQueuePrefetch() {
            return queuePrefetch;
        }

        public void setQueuePrefetch(int queuePrefetch) {
            this.queuePrefetch = queuePrefetch;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("memoryLimit", memoryLimit)
                    .add("sendFailIfNoSpaceAfterTimeout", sendFailIfNoSpaceAfterTimeout)
                    .add("queuePrefetch", queuePrefetch)
                    .toString();
        }
    }

    /**
     * Externalized AMQP Protocol settings.
     */
    public static class AmqpSettings {
        /**
         * A boolean indicating if this transport type should be configured.
         */
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("enabled", enabled)
                    .toString();
        }
    }
}
