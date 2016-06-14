package com.rsa.asoc.sa.ui.common.domain.repository;

import com.google.protobuf.Message;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.Endpoint;
import com.rsa.netwitness.carlos.transport.MessageChannel;
import com.rsa.netwitness.carlos.transport.MessageChannelListener;
import org.apache.commons.io.IOUtils;
import org.springframework.security.core.Authentication;

import java.util.concurrent.CompletableFuture;

/**
 * A base repository for CARLOS-specific implementations.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public abstract class BaseCarlosRepository {

    /**
     * This method handles creating a {@link MessageChannel} from the provided {@link Endpoint}, registering
     * a {@link MessageChannelListener} and sending the request.  The {@link MessageChannel} will be closed
     * automatically when the remote end either responds, or throws an error.
     *
     * @param endpoint the endpoint to work with
     * @param message the Protobuf message to send
     * @param type the type of the Protobuf message
     * @param <T> the type of the Protobuf message
     * @return a {@link CompletableFuture} that will be completed when a response is received from the remote end
     */
    protected <T extends Message> CompletableFuture<T> send(Endpoint endpoint, T message, Class<T> type) {
        CompletableFuture<T> future = new CompletableFuture<>();

        try {
            MessageChannel<T> channel = endpoint.getMessageChannel(type);

            future.handle((ok, ex) -> {
                IOUtils.closeQuietly(channel);
                return ok;
            });

            channel.sendRequest(message, new MessageChannelListener<T>() {
                @Override
                public void onMessage(Authentication authentication, MessageChannel<T> sourceChannel, T message) {
                    future.complete(message);
                }

                @Override
                public void onException(Throwable cause) {
                    future.completeExceptionally(cause);
                }
            });
        }
        catch (Exception e) {
            future.completeExceptionally(e);
        }

        return future;
    }
}
