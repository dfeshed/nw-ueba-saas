package com.rsa.asoc.sa.ui.common.endpoint.domain.repository;

import com.rsa.asoc.sa.ui.common.endpoint.domain.bean.EndpointType;
import com.rsa.netwitness.carlos.common.asg.domain.bean.ApplianceDescriptor;
import com.rsa.netwitness.carlos.common.asg.domain.bean.EndpointDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * A {@link ApplianceEndpointRepository} that retrieves the descriptors from the application's YAML files.  This
 * should only be used in development until we communicate with the ASG service.
 *
 * @author Jay Garala
 */
@Repository
public class YmlApplianceEndpointRepository implements ApplianceEndpointRepository {

    private final YmlApplianceEndpointSettings applianceEndpointSettings;

    @Autowired
    public YmlApplianceEndpointRepository(
            YmlApplianceEndpointSettings applianceEndpointSettings) {
        this.applianceEndpointSettings = applianceEndpointSettings;
    }

    @PostConstruct
    public void init() {
        // Pre-fetch all the ApplianceDescriptions on startup so we don't have to do it for each request
        applianceEndpointSettings.getEndpoints().stream().forEach((endpoint) -> {
                Optional<ApplianceDescriptor> optional = findApplianceById(endpoint.getApplianceId());
                if (optional.isPresent()) {
                    endpoint.setApplianceDescriptor(optional.get());
                }
            });
    }

    @Override
    public CompletableFuture<List<ApplianceDescriptor>> getAppliances() {
        return CompletableFuture.completedFuture(applianceEndpointSettings.getAppliances());
    }

    @Override
    public CompletableFuture<List<EndpointDescriptor>> getEndpoints() {
        return CompletableFuture.completedFuture(applianceEndpointSettings.getEndpoints());
    }

    @Override
    public CompletableFuture<Optional<ApplianceDescriptor>> findById(String id) {
        return CompletableFuture.completedFuture(findApplianceById(id));
    }

    @Override
    public CompletableFuture<List<EndpointDescriptor>> findEndpointsByType(EndpointType type) {
        return CompletableFuture.completedFuture(applianceEndpointSettings.getEndpoints().stream()
                    .filter((endpoint) -> endpoint.getServiceType().name().equals(type.name()))
                    .collect(Collectors.toList()));
    }

    private Optional<ApplianceDescriptor> findApplianceById(String id) {
        return applianceEndpointSettings.getAppliances().stream().filter((app) -> id.equals(app.getId())).findFirst();
    }
}
