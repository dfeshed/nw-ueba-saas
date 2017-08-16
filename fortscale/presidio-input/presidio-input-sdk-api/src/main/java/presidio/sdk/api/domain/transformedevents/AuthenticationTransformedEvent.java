package presidio.sdk.api.domain.transformedevents;

import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;

public class AuthenticationTransformedEvent extends AuthenticationRawEvent {

    public static final String SRC_MACHINE_CLUSTER_FIELD_NAME = "srcMachineCluster";
    public static final String DST_MACHINE_CLUSTER_FIELD_NAME = "dstMachineCluster";

    private String srcMachineCluster;
    private String dstMachineCluster;

    public AuthenticationTransformedEvent(AuthenticationRawEvent rawEvent) {
        super(rawEvent);
    }

    public String getSrcMachineCluster() {
        return srcMachineCluster;
    }

    public void setSrcMachineCluster(String srcMachineCluster) {
        this.srcMachineCluster = srcMachineCluster;
    }

    public String getDstMachineCluster() {
        return dstMachineCluster;
    }

    public void setDstMachineCluster(String dstMachineCluster) {
        this.dstMachineCluster = dstMachineCluster;
    }
}
