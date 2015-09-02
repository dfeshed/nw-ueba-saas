package com.rsa.asoc.sa.ui.threat.web.socket;

import com.rsa.asoc.sa.ui.common.data.Request;
import com.rsa.asoc.sa.ui.common.data.Response;
import com.rsa.asoc.sa.ui.common.data.ResponseCode;
import com.rsa.asoc.sa.ui.threat.domain.bean.Incident;
import com.rsa.asoc.sa.ui.threat.service.IncidentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A web socket controller that handles incident-related messages.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
@Controller
public class IncidentController {

    private static final Log log = LogFactory.getLog(IncidentController.class);

    private final IncidentService incidentService;

    @Autowired
    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @MessageMapping("/threats/incidents")
    @SendToUser(value = "/queue/incidents", broadcast = false)
    public CompletableFuture<Response<List<Incident>>> find(Request request) {
        CompletableFuture<List<Incident>> incidentsFuture = incidentService.findIncidents(request);
        CompletableFuture<Long> countFuture = incidentService.countIncidents(request);

        return incidentsFuture.thenCombine(countFuture, (incidents, count) ->
                new Response<>(incidents, count, request));
    }

    @MessageExceptionHandler
    @SendToUser(value = "/queue/incidents", broadcast = false)
    public Response<?> exceptionHandler(Throwable cause, Request request) {
        log.warn("Failed to retrieve data", cause);
        return new Response<>(ResponseCode.GENERAL_EXCEPTION, request);
    }
}
