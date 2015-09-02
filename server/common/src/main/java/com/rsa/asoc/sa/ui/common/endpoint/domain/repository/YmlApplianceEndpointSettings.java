package com.rsa.asoc.sa.ui.common.endpoint.domain.repository;

import com.rsa.netwitness.carlos.common.asg.domain.bean.ApplianceDescriptor;
import com.rsa.netwitness.carlos.common.asg.domain.bean.EndpointDescriptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Externalized settings for appliances and endpoints.  This should only be used in development.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
@Component
@ConfigurationProperties
public class YmlApplianceEndpointSettings {
    @NestedConfigurationProperty
    private List<ApplianceDescriptor> appliances;

    @NestedConfigurationProperty
    private List<EndpointDescriptor> endpoints;

    public List<ApplianceDescriptor> getAppliances() {
        return appliances;
    }

    public void setAppliances(
            List<ApplianceDescriptor> appliances) {
        this.appliances = appliances;
    }

    public List<EndpointDescriptor> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(
            List<EndpointDescriptor> endpoints) {
        this.endpoints = endpoints;
    }
}
