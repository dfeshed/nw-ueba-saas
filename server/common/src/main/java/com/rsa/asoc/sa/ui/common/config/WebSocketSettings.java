package com.rsa.asoc.sa.ui.common.config;

import com.google.common.base.MoreObjects;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized settings for the web socket.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
@ConfigurationProperties(prefix = "websocket")
public class WebSocketSettings {

    private SockJsSettings sockjs = new SockJsSettings();

    public SockJsSettings getSockjs() {
        return sockjs;
    }

    public void setSockjs(SockJsSettings sockjs) {
        this.sockjs = sockjs;
    }

    /**
     * Externalized SockJS settings.
     */
    public static class SockJsSettings {

        /**
         * The amount of time in milliseconds when the server has not sent any messages and after
         * which the server should send a heartbeat frame to the client in order to keep the
         * connection from breaking.
         */
        private long heartbeatInterval = 10_000L;

        public long getHeartbeatInterval() {
            return heartbeatInterval;
        }

        public void setHeartbeatInterval(long heartbeatInterval) {
            this.heartbeatInterval = heartbeatInterval;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("heartbeatInterval", heartbeatInterval)
                    .toString();
        }
    }
}
