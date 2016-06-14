package com.rsa.asoc.sa.ui.threat.web.socket;

import com.google.common.base.Preconditions;
import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.asoc.sa.ui.common.data.Response;
import com.rsa.asoc.sa.ui.common.data.ResponseCode;
import com.rsa.asoc.sa.ui.common.service.WebSocketSubscriptionService;
import com.rsa.asoc.sa.ui.security.SecurityContextUtils;
import com.rsa.asoc.sa.ui.threat.domain.bean.Incident;
import com.rsa.asoc.sa.ui.threat.service.IncidentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.DestinationUserNameProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * A web socket controller that handles incident-related messages.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
@Controller
public class IncidentController {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentController.class);

    private final IncidentControllerSettings settings;
    private final IncidentService incidentService;
    private final WebSocketSubscriptionService webSocketSubscriptionService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public IncidentController(IncidentControllerSettings settings,
            IncidentService incidentService,
            WebSocketSubscriptionService webSocketSubscriptionService,
            SimpMessagingTemplate simpMessagingTemplate) {
        this.settings = settings;
        this.incidentService = incidentService;
        this.webSocketSubscriptionService = webSocketSubscriptionService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/threats/incidents")
    @SendToUser(broadcast = false)
    public CompletableFuture<Response<List<Incident>>> find(MessageHeaders messageHeaders, Request request) {
        CompletableFuture<List<Incident>> incidentsFuture = incidentService.findIncidents(request);
        CompletableFuture<Long> countFuture = incidentService.countIncidents(request);

        webSocketSubscriptionService.submit(messageHeaders, request, incidentsFuture);
        webSocketSubscriptionService.submit(messageHeaders, request, countFuture);

        return incidentsFuture.thenCombine(countFuture, (incidents, count) ->
                new Response<>(incidents, count, request));
    }

    @MessageMapping("/threats/incidents/stream")
    public void stream(Authentication authentication, MessageHeaders messageHeaders, Request request) {
        Preconditions.checkArgument(request.getStream() != null, "Missing stream property");
        Long limit = request.getStream().getLimit();
        Preconditions.checkArgument(limit != null && limit > 0, "Invalid stream limit: %s", limit);

        // Since IM doesn't support true streaming, we paginate on the server side
        StreamingRequest streamingRequest =
                new StreamingRequest(authentication, messageHeaders, request, settings.getBatchSize());

        Request pagingRequest = streamingRequest.nextPagingRequest();
        CompletableFuture<List<Incident>> incidentsFuture = incidentService.findIncidents(pagingRequest);
        CompletableFuture<Long> countFuture = incidentService.countIncidents(pagingRequest);

        webSocketSubscriptionService.submit(messageHeaders, request, incidentsFuture);
        webSocketSubscriptionService.submit(messageHeaders, request, countFuture);

        CompletableFuture<Response<List<Incident>>> future = incidentsFuture.thenCombine(countFuture,
                (incidents, count) -> {
                streamingRequest.count.set(count);
                // Apply the original request to the response object, not our pagingRequest
                return new Response<>(incidents, count, request);
            });

        webSocketSubscriptionService.submit(messageHeaders, request, streamingRequest::cancel);

        future.thenAccept(responseConsumer(streamingRequest, future));
    }

    @MessageExceptionHandler
    @SendToUser(value = "/queue/threats/incidents", broadcast = false)
    public Response<?> exceptionHandler(Throwable cause, Request request) {
        LOG.warn("Failed to retrieve data", cause);
        Response response = new Response<>(ResponseCode.GENERAL_EXCEPTION, request);
        response.addMeta("message", cause.getMessage());
        return response;
    }

    private MessageHeaders createHeaders(MessageHeaders headers) {
        String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }


    private Consumer<Response<List<Incident>>> responseConsumer(StreamingRequest streamingRequest,
            CompletableFuture<Response<List<Incident>>> future) {

        return response -> {
            if (streamingRequest.isCancelled()) {
                return;
            }

            streamingRequest.sent.addAndGet(response.getData().size());
            simpMessagingTemplate.convertAndSendToUser(streamingRequest.user, "/queue/threats/incidents", response,
                    createHeaders(streamingRequest.messageHeaders));

            // Make another request recursively
            if (streamingRequest.continueSending()) {
                future.thenRunAsync(SecurityContextUtils.wrap(streamingRequest.authentication, () -> {
                    Request pagingRequest = streamingRequest.nextPagingRequest();

                    CompletableFuture<Response<List<Incident>>> responseFuture =
                            incidentService.findIncidents(pagingRequest).thenApply((incidents) ->
                                    new Response<>(incidents, response.getMeta(), streamingRequest.request));

                    responseFuture.thenAccept(responseConsumer(streamingRequest, responseFuture));
                }));
            }
        };
    }

    /**
     * A class to hold the context of streaming requests
     */
    private static class StreamingRequest {
        private final String user;
        private final Authentication authentication;
        private final MessageHeaders messageHeaders;
        private final Request request;
        private final long limit;
        private final int batchSize;

        private final AtomicInteger index = new AtomicInteger(-1);
        private final AtomicLong sent = new AtomicLong(0);
        private final AtomicLong count = new AtomicLong(0);
        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        StreamingRequest(Authentication authentication, MessageHeaders messageHeaders,
                Request request, int batchSize) {
            this.authentication = authentication;
            this.messageHeaders = messageHeaders;
            this.request = request;
            this.batchSize = batchSize;
            this.user = getUserName(messageHeaders);
            this.limit = request.getStream().getLimit();
        }

        private Request nextPagingRequest() {
            return Request.newBuilder(request)
                    .withPage(Request.Page.newBuilder()
                            .withIndex(index.incrementAndGet())
                            .withSize(getNextBatchSize()))
                    .build();
        }

        private String getUserName(MessageHeaders headers) {
            Principal principal = SimpMessageHeaderAccessor.getUser(headers);
            if (principal != null) {
                return principal instanceof DestinationUserNameProvider
                        ? ((DestinationUserNameProvider) principal).getDestinationUserName() : principal.getName();
            }
            return null;
        }

        private int getNextBatchSize() {
            return (int) Math.min((long) batchSize, getRemaining());
        }

        private long getRemaining() {
            return limit - sent.get();
        }

        private boolean hasSentAllRecords() {
            return sent.get() >= count.get();
        }

        private boolean continueSending() {
            return !isCancelled() && !hasSentAllRecords() && getRemaining() > 0;
        }

        private boolean isCancelled() {
            return cancelled.get();
        }

        public void cancel() {
            cancelled.set(true);
        }
    }
}
