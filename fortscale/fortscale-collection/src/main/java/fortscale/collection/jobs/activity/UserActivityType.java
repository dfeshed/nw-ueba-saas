package fortscale.collection.jobs.activity;

import fortscale.domain.core.activities.UserActivityDocument;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.domain.core.activities.UserActivityNetworkAuthenticationDocument;
import fortscale.domain.core.activities.UserActivitySourceMachineDocument;

/**
 * @author gils
 * 31/05/2016
 */
public enum UserActivityType {
    LOCATIONS(UserActivityLocationDocument.class),
    NETWORK_AUTHENTICATION(UserActivityNetworkAuthenticationDocument.class),
    SOURCE_MACHINE(UserActivitySourceMachineDocument.class);

    Class<? extends UserActivityDocument> documentClass;
    UserActivityType(Class<? extends UserActivityDocument> clazz){
        this.documentClass = clazz;
    }

    public Class<? extends UserActivityDocument> getDocumentClass() {
        return documentClass;
    }
}
