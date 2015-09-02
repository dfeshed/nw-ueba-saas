package com.rsa.asoc.sa.ui.common.endpoint.domain.repository;

import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.EndpointType;
import com.rsa.netwitness.carlos.common.asg.domain.bean.ApplianceDescriptor;
import com.rsa.netwitness.carlos.common.asg.domain.bean.EndpointDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

/**
 * Tests for {@link YmlApplianceEndpointRepository}
 *
 * @author Jay Garala
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
@TestPropertySource("/com/rsa/asoc/sa/ui/common/endpoint/test-endpoint.properties")
public class YmlApplianceEndpointRepositoryTest {

    @Autowired
    private YmlApplianceEndpointRepository ymlApplianceEndpointRepository;

    @Configuration
    @EnableAutoConfiguration
    static class Config {
        @Bean
        public YmlApplianceEndpointSettings applianceEndpointSettings() {
            return new YmlApplianceEndpointSettings();
        }

        @Bean
        public YmlApplianceEndpointRepository ymlRepository() {
            return new YmlApplianceEndpointRepository(applianceEndpointSettings());
        }
    }

    @Test
    public void testGetAppliances() throws Exception {
        CompletableFuture<List<ApplianceDescriptor>> future = ymlApplianceEndpointRepository.getAppliances();

        assertTrue(future.isDone());
        List<ApplianceDescriptor> applianceDescriptors = future.get();
        assertEquals("Did not find one appliance", 1 , applianceDescriptors.size());
    }

    @Test
    public void testFindById() throws Exception {
        final String id = "555d9a6fe4b0d37c827d402d";
        CompletableFuture<Optional<ApplianceDescriptor>> future =
                ymlApplianceEndpointRepository.findById(id);

        assertTrue(future.isDone());
        Optional<ApplianceDescriptor> optional = future.get();

        assertTrue("Could not find the appliance with id '555d9a6fe4b0d37c827d402d'", optional.isPresent());
        assertEquals(id, optional.get().getId());
    }

    @Test
    public void testFindByIdWithNoResults() throws Exception {
        final String id = "foo";
        CompletableFuture<Optional<ApplianceDescriptor>> future =
                ymlApplianceEndpointRepository.findById(id);

        assertTrue(future.isDone());
        Optional<ApplianceDescriptor> optional = future.get();

        assertFalse(optional.isPresent());
    }

    @Test
    public void testGetEndpoints() throws Exception {
        CompletableFuture<List<EndpointDescriptor>> future = ymlApplianceEndpointRepository.getEndpoints();

        assertTrue(future.isDone());
        List<EndpointDescriptor> endpointDescriptors = future.get();
        assertEquals("Did not find a single endpoint", 1 , endpointDescriptors.size());
    }

    @Test
    public void testFindEndpointsByType() throws Exception {
        CompletableFuture<List<EndpointDescriptor>> future =
                ymlApplianceEndpointRepository.findEndpointsByType(EndpointType.INCIDENT_MANAGEMENT);

        assertTrue(future.isDone());
        List<EndpointDescriptor> endpointDescriptors = future.get();
        assertEquals("IM endpoint does not exist", 1 , endpointDescriptors.size());
    }

    @Test
    public void testFindEndpointsByTypeWithNoResults() throws Exception {
        CompletableFuture<List<EndpointDescriptor>> future =
                ymlApplianceEndpointRepository.findEndpointsByType(EndpointType.EVENT_STREAM_ANALYSIS);

        assertTrue(future.isDone());
        List<EndpointDescriptor> endpointDescriptors = future.get();
        assertEquals("ESA endpoint does not exist", 0 , endpointDescriptors.size());
    }
}