package com.rsa.asoc.sa.ui.common.endpoint;

import com.rsa.asoc.sa.admin.ApplianceServiceTypeProtocol;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.Endpoint;
import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.EndpointType;
import com.rsa.asoc.sa.ui.common.endpoint.domain.repository.ApplianceEndpointRepository;
import com.rsa.netwitness.carlos.common.asg.domain.bean.ApplianceDescriptor;
import com.rsa.netwitness.carlos.common.asg.domain.bean.EndpointDescriptor;
import com.rsa.netwitness.carlos.transport.MessageEndpointFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests {@link DefaultEndpointService}
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public class DefaultEndpointServiceTest {

    @Mock
    private ApplianceEndpointRepository applianceEndpointRepository;

    @Mock
    private MessageEndpointFactory messageEndpointFactory;

    private DefaultEndpointService defaultEndpointService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        defaultEndpointService = new DefaultEndpointService(applianceEndpointRepository, messageEndpointFactory);
    }

    @Test
    public void testGetEndpointsByType() throws Exception {
        List<EndpointDescriptor> descriptors = Collections.singletonList(
                createEndpointDescriptor(ApplianceServiceTypeProtocol.ServiceType.INCIDENT_MANAGEMENT));
        when(applianceEndpointRepository.findEndpointsByType(any(EndpointType.class)))
                .thenReturn(CompletableFuture.completedFuture(descriptors));

        CompletableFuture<List<Endpoint>> future =
                defaultEndpointService.getEndpointsByType(EndpointType.INCIDENT_MANAGEMENT);

        assertTrue(future.isDone());
        List<Endpoint> endpoints = future.get();

        assertEquals(1, endpoints.size());
    }

    @Test
    public void testNoEndpointsWithThatType() throws Exception {
        when(applianceEndpointRepository.findEndpointsByType(any(EndpointType.class)))
                .thenReturn(CompletableFuture.completedFuture(Collections.<EndpointDescriptor>emptyList()));

        CompletableFuture<List<Endpoint>> future =
                defaultEndpointService.getEndpointsByType(EndpointType.CONCENTRATOR);

        assertTrue(future.isDone());
        List<Endpoint> endpoints = future.get();

        assertTrue(endpoints.isEmpty());
    }

    private EndpointDescriptor createEndpointDescriptor(ApplianceServiceTypeProtocol.ServiceType type) {
        ApplianceDescriptor applianceDescriptor1 = new ApplianceDescriptor();
        applianceDescriptor1.setId(UUID.randomUUID().toString());
        applianceDescriptor1.setHost("127.0.0.1");

        EndpointDescriptor endpointDescriptor = new EndpointDescriptor();
        endpointDescriptor.setId(UUID.randomUUID().toString());
        endpointDescriptor.setName("Testing Endpoint");
        endpointDescriptor.setServiceType(type);
        endpointDescriptor.setApplianceDescriptor(applianceDescriptor1);

        return endpointDescriptor;
    }
}
