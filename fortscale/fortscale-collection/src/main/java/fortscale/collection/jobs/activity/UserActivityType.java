package fortscale.collection.jobs.activity;

import fortscale.domain.core.activities.*;

public enum UserActivityType {

    LOCATIONS(UserActivityLocationDocument.class),
    NETWORK_AUTHENTICATION(UserActivityNetworkAuthenticationDocument.class),
    SOURCE_MACHINE(UserActivitySourceMachineDocument.class),
    TARGET_DEVICE(UserActivityTargetDeviceDocument.class),
    WORKING_HOUR(UserActivityWorkingHoursDocument.class),
    DATA_USAGE(UserActivityDataUsageDocument.class),
    TOP_APPLICATIONS(UserActivityTopApplicationsDocument.class);

    private final Class<? extends UserActivityDocument> documentClass;

    UserActivityType(Class<? extends UserActivityDocument> clazz) {
        this.documentClass = clazz;
    }

    public Class<? extends UserActivityDocument> getDocumentClass() {
        return documentClass;
    }

}