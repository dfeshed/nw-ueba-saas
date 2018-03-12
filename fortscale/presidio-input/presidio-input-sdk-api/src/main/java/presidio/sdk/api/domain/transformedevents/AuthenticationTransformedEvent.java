package presidio.sdk.api.domain.transformedevents;

import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;

public class AuthenticationTransformedEvent extends AuthenticationRawEvent {

    public static final String SRC_MACHINE_CLUSTER_FIELD_NAME = "srcMachineCluster";
    public static final String RESOURCE_CLUSTER_FIELD_NAME = "resourceCluster";

    private String srcMachineCluster;
    private String resourceCluster;

    public AuthenticationTransformedEvent(AuthenticationRawEvent rawEvent) {
        super(rawEvent);
    }

    public String getSrcMachineCluster() {
        return srcMachineCluster;
    }

    public void setSrcMachineCluster(String srcMachineCluster) {
        this.srcMachineCluster = srcMachineCluster;
    }

    public String getResourceCluster() {
        return resourceCluster;
    }

    public void setResourceCluster(String resourceCluster) {
        this.resourceCluster = resourceCluster;
    }
}
