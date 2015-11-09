package com.rsa.asoc.sa.ui.threat.web.socket;

import com.google.common.base.Strings;
import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.asoc.sa.ui.common.data.Response;
import com.rsa.asoc.sa.ui.common.data.ResponseCode;
import com.rsa.asoc.sa.ui.common.service.WebSocketSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

/**
 * A controller to explicitly cancel any long running WebSocket requests.
 *
 * @author Abram Thielke
 * @since 10.6.0
 */
@Controller
public class CancelController {

    private final WebSocketSubscriptionService webSocketSubscriptionService;

    @Autowired
    public CancelController(WebSocketSubscriptionService webSocketSubscriptionService) {
        this.webSocketSubscriptionService = webSocketSubscriptionService;
    }

    @MessageMapping("/threats/cancel")
    @SendToUser(broadcast = false)
    public Response<?> cancel(MessageHeaders messageHeaders, Request request) {
        String requestId = request.getId();
        if (!Strings.isNullOrEmpty(requestId)) {
            webSocketSubscriptionService.cleanupRequest(messageHeaders, requestId);
        }
        return new Response<>(ResponseCode.SUCCESS, request);
    }
}
