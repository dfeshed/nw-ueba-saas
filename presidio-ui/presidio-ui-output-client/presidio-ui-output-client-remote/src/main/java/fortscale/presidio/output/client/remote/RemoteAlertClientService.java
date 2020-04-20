package fortscale.presidio.output.client.remote;

import fortscale.presidio.output.client.api.AlertsPresidioOutputClient;
import presidio.output.client.api.AlertsApi;
import presidio.output.client.client.ApiClient;

public class RemoteAlertClientService extends RemoteClientServiceAbs<AlertsApi>  implements AlertsPresidioOutputClient {

    @Override
    protected AlertsApi getControllerInstance(ApiClient delegatoeApiClient) {
        return new AlertsApi(delegatoeApiClient);
    }

}
